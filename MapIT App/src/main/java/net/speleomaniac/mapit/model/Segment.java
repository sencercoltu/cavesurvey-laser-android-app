package net.speleomaniac.mapit.model;

/**
 * Created by Sencer Coltu on 1.9.2014.
 */
public class Segment {
    public Point P0;
    public Point P1;

    //bir segmentte sadece 2 surface olabilir
    public Surface S0;
    public Surface S1;

    public static Segment EmptySegment() {
        return new Segment(Point.EmptyPoint(), Point.EmptyPoint());
    }

    public Segment(Point p0, Point p1) {
        P0 = p0;
        P1 = p1;
        P0.addSegment(this);
        P1.addSegment(this);
    }

    public Vector vectorize() {
        return new Vector(P1.X - P0.X, P1.Y - P0.Y, P1.Z - P0.Z);
    }


}
