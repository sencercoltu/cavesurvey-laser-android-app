package net.speleomaniac.mapit.model;

import java.util.ArrayList;

/**
 * Created by Sencer Coltu on 1.9.2014.
 */
public class Point {
    public float X;
    public float Y;
    public float Z;

    public ArrayList<Segment> Segments = new ArrayList<Segment>();

    public static final Point NullPoint = EmptyPoint();

    public static Point EmptyPoint() {
        return new Point(0, 0, 0);
    }

    public Point(float x, float y, float z) {
        X = x;
        Y = y;
        Z = z;
    }

    public void addSegment(Segment s) {
        if (!Segments.contains(s))
            Segments.add(s);
    }


    public Vector add(Point p) {
        return new Vector(X + p.X, Y + p.Y, Z + p.Z);
    }

    public Vector subtract(Point p) {
        return new Vector(X - p.X, Y - p.Y, Z - p.Z);
    }

    public Point add(Vector v) {
        return new Point(X + v.X, Y + v.Y, Z + v.Z);
    }

    public Point product(float k) {
        return new Point(X * k, Y * k, Z * k);
    }

}
