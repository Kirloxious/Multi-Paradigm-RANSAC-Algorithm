// Author: Alexandre Ringuette
// Student #: 300251252
package main

import (
	"math/rand"
	"sync"
)

func RandomPoints(points []Point3D) Point3D {
	randIndex := rand.Intn(len(points))

	return points[randIndex]
}

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
				break
			case outputPointStream <- <-inputPointStream:
			}
		}
	}()

	return outputPointStream
}

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

func DominantPlaneIdentifier(inputPlaneSupportStream <-chan Plane3DwSupport, bestSupport *Plane3DwSupport) {

	for i := range inputPlaneSupportStream {
		if bestSupport.SupportSize < i.SupportSize {
			*bestSupport = i
		}
	}
}
