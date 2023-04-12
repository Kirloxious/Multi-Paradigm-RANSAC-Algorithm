%Alexandre Ringuette, 300251252
read_xyz_file(File, Points) :-
        open(File, read, Stream),
        read_xyz_points(Stream,Points),
    close(Stream).
read_xyz_points(Stream, []) :-
    at_end_of_stream(Stream).
read_xyz_points(Stream, [Point|Points]) :-
    \+ at_end_of_stream(Stream),
    read_line_to_string(Stream,L), split_string(L, "\t", "\s\t\n",
XYZ), convert_to_float(XYZ,Point),
read_xyz_points(Stream, Points).

convert_to_float([],[]).
convert_to_float([H|T],[HH|TT]) :-
    atom_number(H, HH),
    convert_to_float(T,TT).

append_list([], L2, [L2]).    
append_list([X | L1], L2, [X | L3]) :-
    append_list(L1, L2, L3).

%This predicate should be true if Point3 is a triplet of points randomly selected from
% the list of points Points. The triplet of points is of the form [[x1,y1,z1],
% [x2,y2,z2], [x3,y3,z3]].
random3points(Points, Point3):-
    [] = Temp,
    random_member(X, Points), append_list(Temp, X, Res1),
    random_member(Y, Points), append_list(Res1, Y, Res2),
    random_member(Z, Points), append_list(Res2, Z, Point3).

%This predicate should be true if Plane is the equation of the plane defined by the three
% points of the list Point3. The plane is specified by the list [a,b,c,d] from the
% equation ax+by+cz=d. The list of points is of the form [[x1,y1,z1],
% [x2,y2,z2], [x3,y3,z3]].
plane([],[]).
plane(Point3, Plane):-
    nth0(0, Point3, P1), nth0(1, Point3, P2), nth0(2, Point3, P3),
    point_vector(P1, P2, VecP1P2), point_vector(P1, P3, VecP1P3),
    nth0(0, VecP1P2, X1), nth0(1, VecP1P2, Y1), nth0(2, VecP1P2, Z1),
    nth0(0, VecP1P3, X2), nth0(1, VecP1P3, Y2), nth0(2, VecP1P3, Z2),
    A is (Y1*Z2)-(Z1*Y2), B is (Z1*X2)-(X1*Z2), C is (X1*Y2)-(Y1*X2),
    nth0(0, P3, P3X), nth0(1, P3, P3Y), nth0(2, P3, P3Z),
    D is 0 - (A * P3X) - (B * P3Y) - (C*P3Z), 
    [A, B, C, D] = Plane.

%This predicate is true when Vector is the vector point of Point1 and Point2
point_vector([],[],[]).
point_vector([P1|Point1], [P2|Point2], [V|Vector]):-
    point_vector(Point1, Point2, Vector),
    V is P2 - P1.

%test(R):- plane([[-5.1323336,-4.089636333,0.243960825],[-6.771682784,-4.842419765,0.837837612],[5.002873989,-1.90693905,0.343599434]], R).
%R = 
%same result as java

%This predicate is true when N is the number of supporting points of Plane when the distance is smaller then Eps
support(_, [], _, 0).
support(Plane, [P|Points], Eps, N):-
    support(Plane, Points, Eps, NN),
    distance(Plane, P, D), distance_eps(D, Eps, R), N is NN + R.

distance_eps(D, Eps, R):-
    D < Eps, R = 1.
distance_eps(D, Eps, R):-
    D > Eps, R = 0.

%This predicate is true when N is the distance between Plane and Point
distance(Plane, Point, N):-
    nth0(0, Point, X), nth0(1, Point, Y), nth0(2, Point, Z),
    nth0(0, Plane, A), nth0(1, Plane, B), nth0(2, Plane, C), nth0(3, Plane, D),
    N is (abs((A*X)+ (B*Y) + (C*Z)+D))/sqrt((A**2)+(B**2)+(C*22)).

%This predicate is true when N is the number of iterations with the given Confidence and Percentage.
ransac_number_of_iterations(Confidence, Percentage, N):-
    N is integer(log(1-Confidence)/(log(1-(Percentage**3)))).


%Java returns plane = [-1.3712595434643953, 6.182406990892381, 4.051413343074801, 17.257648684511164]
%with the given points
test(plane, 1):- plane([[-5.1323336,-4.089636333,0.243960825],[-6.771682784,-4.842419765,0.837837612],[5.002873989,-1.90693905,0.343599434]], 
                        [-1.3712595434643953, 6.182406990892381, 4.051413343074801, 17.257648684511164]).
test(plane, 2):- plane([],[]).

%Plane [20, -70, -50, -490] created in java using point [1, 3, 6] whichs means distance should be 0
test(distance, 1):-distance([20, -70, -50, 490], [1, 3, 6], 0).

%Java functions returns 4603 iterations for confidence = 0.99 & Percentage = 0.1
test(iterations, 1):- ransac_number_of_iterations(0.99, 0.1, 4603).

%Java support count for plane = [-1.3712595434643953, 6.182406990892381, 4.051413343074801, 17.257648684511164] is 108
test(support, 1):- read_xyz_file('Point_Cloud_1_No_Road_Reduced.xyz', Points),support([-1.3712595434643953, 6.182406990892381, 4.051413343074801, 17.257648684511164], Points, 0.1, 108).

%checks if an element in Xs is also an element of Ys.
common_member(Xs,Ys) :-
    member(E,Xs),
    member(E,Ys).
test(random3points, 1):- read_xyz_file('Point_Cloud_1_No_Road_Reduced.xyz', Points), random3points(Points, Point3), random3points(Points, Point32), not(common_member(Point3, Point32)).
%TODO: finish writing test cases
