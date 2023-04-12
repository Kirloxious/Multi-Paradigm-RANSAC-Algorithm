;Alexandre Ringuette, 300251252
#lang scheme
;Reads the XYZ file and returns the points in a list
(define (readXYZ fileIn)
  (let ((sL (map (lambda s (string-split (car s)))
                           (cdr (file->lines fileIn)))))
    (map (lambda (L)
           (map (lambda (s)
                  (if (eqv? (string->number s) #f)
                      s
                      (string->number s))) L)) sL)))

;Calculations the point vector of 2 points
(define (point-vector P1 P2)
  (list (-(list-ref P1 0)(list-ref P2 0))
        (-(list-ref P1 1)(list-ref P2 1))
        (-(list-ref P1 2)(list-ref P2 2))
    )
  ) 

;Calculates the plane equation of 3 points
;ax + by + cz = d
(define (plane P1 P2 P3)
  (let* ((vecP1P2 (point-vector P1 P2))
          (vecP1P3 (point-vector P1 P3))
          (a (-(*(list-ref vecP1P2 1)(list-ref vecP1P3 2))(*(list-ref vecP1P2 2)(list-ref vecP1P3 1))))
          (b (-(*(list-ref vecP1P2 2)(list-ref vecP1P3 0))(*(list-ref vecP1P2 0)(list-ref vecP1P3 2))))
          (c (-(*(list-ref vecP1P2 0)(list-ref vecP1P3 1))(*(list-ref vecP1P2 1)(list-ref vecP1P3 0))))
          (d (+(* a (- 0 (list-ref P3 0)))(* b (- 0 (list-ref P3 1)))(* c (- 0 (list-ref P3 2)))))
          )
    (list a b c d))
  )

;Calculates the distance between a point and a plane
(define (distance plane point)
  (let* ((a (list-ref plane 0))
         (b (list-ref plane 1))
         (c (list-ref plane 2))
         (d (list-ref plane 3))
         (x (list-ref point 0))
         (y (list-ref point 1))
         (z (list-ref point 2)))
    (/(abs (+ (* a x) (* b y) (* c z) d))(sqrt (+ (expt a 2)(expt b 2)(expt c 2)))))
  )

;Gets the number of points that support the plane
(define (support plane points eps)
  (cons plane (length (filter (lambda (point) (< (distance plane point) eps)) points)))
  )

;Calculates the number of iterations
(define (ransacNumberOfIterations confidence percentage)
  (round (/ (log (- 1 confidence)) (log (- 1 (expt percentage 3)))))
  )

;main dominantPlane function to call
(define (dominantPlane Ps k eps)
  (dominantPlane-helper Ps k eps '(0 . 0))
  )

;dominantPlane helper function which finds the plane with the most supporting points
(define (dominantPlane-helper Ps k eps best)
  (let* ((Ps3 (list (list-ref Ps (random (length Ps))) 
              (list-ref Ps (random (length Ps))) 
              (list-ref Ps (random (length Ps)))))
          (sup (support (plane (list-ref Ps3 0)(list-ref Ps3 1)(list-ref Ps3 2)) Ps eps)))
    (cond 
        ((= 0 k) best)
        ((> (cdr sup) (cdr best)) (dominantPlane-helper Ps (- k 1) eps sup))
        (else (dominantPlane-helper Ps (- k 1) eps best)))
        )
  )

;main ransac function
;returns the pair (a b c d).n whichs is the most dominant plane
(define (planeRANSAC filename confidence percentage eps)
  (let* ((Ps (readXYZ filename))
         (k (ransacNumberOfIterations confidence percentage)))
    (dominantPlane Ps k eps)
    )
  )


;TODO: Write test cases for each function


(planeRANSAC "Point_Cloud_1_No_Road_Reduced.xyz" 0.99 0.1 0.1) ;java returns 1526 supporting points for the same plane
;Point Cloud 1 : ((11.451981115411385 -0.401344603075792 -0.14529233177506273 -131.4481507662455) . 1526)
(planeRANSAC "Point_Cloud_2_No_Road_Reduced.xyz" 0.99 0.1 0.1) ;java returns 2621 supporting points for the same plane
;Point Cloud 2 : ((-5.416253924878839 -0.01412168687977302 0.33014870687097164 13.283912346096736) . 2621)
(planeRANSAC "Point_Cloud_3_No_Road_Reduced.xyz" 0.99 0.1 0.1) ;java returns 1474 supporting points for the same plane
;Point Cloud 3 :((-1.6099630005203334 -0.02342579710966481 0.06264067474426496 -4.157899868030666) . 1474)