// Author: Alexandre Ringuette
// Student #: 300251252
package main

import (
	"bufio"
	"fmt"
	"log"
	"math"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"sync"
	"time"
)

type Point3D struct {
	X float64
	Y float64
	Z float64
}

type Plane3D struct {
	A float64
	B float64
	C float64
	D float64
}

type Plane3DwSupport struct {
	Plane3D
	SupportSize int
}

// reads an XYZ file and returns a slice
func ReadXYZ(filename string) []Point3D {
	var ptSlice []Point3D
	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()
		splice := strings.Split(line, "\t")
		x, _ := strconv.ParseFloat(splice[0], 64)
		y, _ := strconv.ParseFloat(splice[1], 64)
		z, _ := strconv.ParseFloat(splice[2], 64)
		if x != 0 && y != 0 && z != 0 {
			point := Point3D{x, y, z}
			ptSlice = append(ptSlice, point)
		}
	}

	return ptSlice
}

// saves a slice of Point3D into an XYZ file
func SaveXYZ(filename string, points []Point3D) {
	f, err := os.Create(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()
	fmt.Fprintf(f, "x\ty\tz\n")
	for _, pt := range points {
		fmt.Fprintf(f, "%v\t%v\t%v\n", pt.X, pt.Y, pt.Z)
	}

}

// computes the distance between point p1 and a plane
func (plane *Plane3D) GetDistance(p1 *Point3D) float64 {

	numerator := math.Abs((plane.A * p1.X) + (plane.B * p1.Y) + (plane.C * p1.Z) + plane.D)
	denomenator := math.Sqrt(math.Pow(plane.A, 2) + math.Pow(plane.B, 2) + math.Pow(plane.C, 2))

	return numerator / denomenator
}

// calculate the vector between two points
func (p1 *Point3D) CalculatePointVector(p2 *Point3D) Point3D {
	return Point3D{p2.X - p1.X, p2.Y - p1.Y, p2.Z - p1.Z}
}

// computes the plane defined by a slice of 3 points
func GetPlane(points []Point3D) Plane3D {
	vecP1P2 := points[0].CalculatePointVector(&points[1])
	vecP1P3 := points[0].CalculatePointVector(&points[2])

	a := vecP1P2.Y*vecP1P3.Z - vecP1P2.Z*vecP1P3.Y
	b := vecP1P2.Z*vecP1P3.X - vecP1P2.X*vecP1P3.Z
	c := vecP1P2.X*vecP1P3.Y - vecP1P2.Y*vecP1P3.X
	d := (a * -points[2].X) + (b * -points[2].Y) + (c * -points[2].Z)

	return Plane3D{a, b, c, d}

}

// computes the number of required RANSAC iterations
func GetNumberOfIterations(confidence float64, percentageOfPointsOnPlane float64) int {
	return (int)((math.Log(1 - confidence)) / (math.Log(1 - math.Pow(percentageOfPointsOnPlane, 3))))

}

// computes the support of a plane in a slice of points
func GetSupport(plane Plane3D, points *[]Point3D, eps float64) Plane3DwSupport {
	var count int
	for _, pt := range *points {
		distance := plane.GetDistance(&pt)
		if distance < eps {
			count++
		}
	}
	return Plane3DwSupport{plane, count}

}

// extracts the points that supports the given plane
// and returns them in a slice of points
func GetSupportingPoints(plane Plane3D, points *[]Point3D, eps float64) []Point3D {
	var ptSlice []Point3D
	for _, pt := range *points {
		distance := plane.GetDistance(&pt)
		if distance < eps {
			ptSlice = append(ptSlice, pt)
		}
	}
	return ptSlice

}

// creates a new slice of points in which all points
// belonging to the plane have been removed
func RemovePlane(plane Plane3D, points *[]Point3D, eps float64) []Point3D {
	var ptSlice []Point3D
	for _, pt := range *points {
		distance := plane.GetDistance(&pt)
		if distance > eps {
			ptSlice = append(ptSlice, pt)
		}
	}
	return ptSlice

}

func main() {

	rand.Seed(int64(time.Now().Nanosecond())) //call to gen unique value for rand object

	FILENAME := os.Args[1]
	fmt.Println(FILENAME)
	shortFileName := strings.Split(FILENAME, ".xyz")
	points := ReadXYZ(FILENAME)
	confidence, _ := strconv.ParseFloat(os.Args[2], 64)
	percentageOfPoints, _ := strconv.ParseFloat(os.Args[3], 64)
	eps, _ := strconv.ParseFloat(os.Args[4], 64)
	start := time.Now()

	for cloud := 1; cloud <= 3; cloud++ {
		stop := make(chan bool)

		numberOfIterations := GetNumberOfIterations(confidence, percentageOfPoints)

		var bestSupport Plane3DwSupport = Plane3DwSupport{Plane3D{0, 0, 0, 0}, 0}
		var wg sync.WaitGroup
		wg.Add(4)

		planeStream := PlaneEstimator(&wg, stop,
			TakeN(&wg, stop,
				TripletPointGen(&wg, stop,
					GenRandomPoints(&wg, stop, &points, RandomPoints)), numberOfIterations))

		fanOut := 16
		wg.Add(fanOut)
		supportingPointsStream := make([]<-chan Plane3DwSupport, fanOut)
		for i := 0; i < fanOut; i++ {
			supportingPointsStream[i] = SupportingPointsFinder(&wg, stop, planeStream, &points, eps)
		}

		wg.Add(1)
		DominantPlaneIdentifier(&wg, stop, FanIn(&wg, stop, supportingPointsStream), &bestSupport)

		close(stop)
		wg.Wait()

		bestPlane := bestSupport.Plane3D
		bestPlanePoints := GetSupportingPoints(bestPlane, &points, eps)
		fmt.Printf("Best Support plane: %v\nBest Support Count: %d\n", bestPlane, bestSupport.SupportSize)
		saveFileName := shortFileName[0] + "_p" + strconv.FormatInt(int64(cloud), 10) + ".xyz"
		SaveXYZ(saveFileName, bestPlanePoints)
		points = RemovePlane(bestPlane, &points, eps)
		fmt.Printf("End of Execution #%d\n", cloud)
	}

	fmt.Println("\nEnd of program")
	end := time.Now()
	totalRuntime := end.Sub(start)
	fmt.Printf("\nTotal Runtime: %s\n", totalRuntime)

}
