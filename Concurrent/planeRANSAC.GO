package main	

import (
	"os"
	"log"
	"bufio"
	"fmt"
	"strings"
	"strconv"
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

//reads an XYZ file and returns a slice
func ReadXYZ(filename string)[]Point3D{

	file, err := os.Open(filename)
	if err!= nil{
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
		point := Point3D{x, y, z}
		fmt.Println(point)
	}

	return nil
}

// saves a slice of Point3D into an XYZ file
func SaveXYZ(filename string, points []Point3D){
}

// computes the distance between points p1 and p2
func (p1 *Point3D) GetDistance(p2 *Point3D) float64 {
	return 1

}

// computes the plane defined by a slice of 3 points
func GetPlane(points []Point3D) Plane3D {
	return Plane3D{}

}

// computes the number of required RANSAC iterations
func GetNumberOfIterations(confidence float64, percentageOfPointsOnPlane float64) int{
	return 1

}

// computes the support of a plane in a slice of points
func GetSupport(plane Plane3D, points []Point3D, eps float64) Plane3DwSupport {
	return Plane3DwSupport{}

}

// extracts the points that supports the given plane
// and returns them in a slice of points
func GetSupportingPoints(plane Plane3D, points []Point3D, eps float64) []Point3D {
	return []Point3D{}

}

// creates a new slice of points in which all points
// belonging to the plane have been removed
func RemovePlane(plane Plane3D, points []Point3D, eps float64) []Point3D {
	return  []Point3D{}

}

func main(){
	ReadXYZ("PointCloud2.xyz")
}