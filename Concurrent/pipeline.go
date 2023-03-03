// Author: Alexandre Ringuette
// Student #: 300251252
package main

import (
	"math/rand"
	"sync"
)

// Returns a random point in the point splice
func RandomPoints(points []Point3D) Point3D {
	randIndex := rand.Intn(len(points))
	return points[randIndex]
}

// Generates randoms points from the provided slice of points.
// Outputs channel transmits instances of Point3D
func GenRandomPoints(wg *sync.WaitGroup, stop <-chan bool, points *[]Point3D, fct func([]Point3D) Point3D) <-chan Point3D {
	pointStream := make(chan Point3D)

	go func() {
		defer func() {
			close(pointStream)
			wg.Done()
		}()
		for {
			select {
			case <-stop:
				return
			case pointStream <- fct(*points):
			}
		}
	}()
	return pointStream
}

// Reads Point3D instances from input channel and accumulate 3 points
// Output channel transmits arrays of Point3D
func TripletPointGen(wg *sync.WaitGroup, stop <-chan bool, inputPointStream <-chan Point3D) <-chan []Point3D {
	outputPointStream := make(chan []Point3D)

	go func() {
		defer func() {
			close(outputPointStream)
			wg.Done()
		}()
		var pointSlice []Point3D
		for i := range inputPointStream {
			pointSlice = append(pointSlice, i)
			if len(pointSlice) == 3 {
				select {
				case <-stop:
					return
				case outputPointStream <- pointSlice:
				}
				pointSlice = make([]Point3D, 0)
			}
		}
	}()

	return outputPointStream
}

// Reads arrays of Point3D and resends them.
// Automatically stops the pipeline after received N arrays
func TakeN(wg *sync.WaitGroup, stop <-chan bool, inputPointStream <-chan []Point3D, n int) <-chan []Point3D {
	outputPointStream := make(chan []Point3D)

	go func() {
		defer func() {
			close(outputPointStream)
			defer wg.Done()
		}()
		for i := 0; i < n; i++ {
			select {
			case <-stop:
				return
			case outputPointStream <- <-inputPointStream:
			}
		}
	}()

	return outputPointStream
}

// Reads arrays of three Point3D and compute the plane defined by these points.
// Output channel transmits Plane3D instances.
func PlaneEstimator(wg *sync.WaitGroup, stop <-chan bool, inputPointStream <-chan []Point3D) <-chan Plane3D {
	outputPlaneStream := make(chan Plane3D)

	go func() {
		defer func() {
			close(outputPlaneStream)
			wg.Done()
		}()
		for i := range inputPointStream {
			if len(i) != 0 {
				plane := GetPlane(i)
				select {
				case <-stop:
					return
				case outputPlaneStream <- plane:
				}
			}
		}
	}()

	return outputPlaneStream
}

// Counts the number of points in the provided slice Point3D(input point cloud) that support the receied Plane3D
// Output channel transmits the Plane3D and the number of supporting points in a Point3DwSupport instances.
func SupportingPointsFinder(wg *sync.WaitGroup, stop <-chan bool, inputPlaneStream <-chan Plane3D, pointCloud *[]Point3D, eps float64) <-chan Plane3DwSupport {
	outputPlaneStream := make(chan Plane3DwSupport)

	go func() {
		defer func() {
			close(outputPlaneStream)
			wg.Done()
		}()
		for i := range inputPlaneStream {
			select {
			case <-stop:
				return
			case outputPlaneStream <- GetSupport(i, pointCloud, eps):
			}
		}

	}()

	return outputPlaneStream
}

// Multiplexes the results received from multiple channels into one output channel
func FanIn(wg *sync.WaitGroup, stop <-chan bool, channels []<-chan Plane3DwSupport) <-chan Plane3DwSupport {
	var multiplexGroup sync.WaitGroup
	outputPlaneSupportStream := make(chan Plane3DwSupport)

	reader := func(ch <-chan Plane3DwSupport) {
		defer func() { multiplexGroup.Done() }()
		for i := range ch {
			select {
			case <-stop:
				return
			case outputPlaneSupportStream <- i:
			}
		}
	}

	multiplexGroup.Add(len(channels))
	for _, ch := range channels {
		go reader(ch)
	}

	go func() {
		defer func() {
			close(outputPlaneSupportStream)
			wg.Done()
		}()
		multiplexGroup.Wait()
	}()

	return outputPlaneSupportStream
}

// Receives Plane3DwSupport instances and keeps in memory the plane with the best support
func DominantPlaneIdentifier(wg *sync.WaitGroup, stop <-chan bool, inputPlaneSupportStream <-chan Plane3DwSupport, bestSupport *Plane3DwSupport) {

	for i := range inputPlaneSupportStream {
		if bestSupport.SupportSize < i.SupportSize {
			*bestSupport = i
		}
	}
}
