package net.speleomaniac.mapit.model;

/**
 * Created by Sencer Coltu on 1.9.2014.
 */
public class Surface {
    public Segment S0;
    public Segment S1;
    public Segment S2;

    public Point P0() {
        return S0.P0;
    }

    public Point P1() {
        return S1.P0;
    }

    public Point P2() {
        return S2.P0;
    }

    public Vector N0 = Vector.EmptyVector();
    public Vector N1 = Vector.EmptyVector();
    public Vector N2 = Vector.EmptyVector();

    public static final Surface NullSurface = EmptySurface();

    public static Surface EmptySurface() {
        return new Surface(Segment.EmptySegment(), Segment.EmptySegment(), Segment.EmptySegment());
    }

    public Surface(Segment s0, Segment s1, Segment s2) {
        S0 = s0;
        S1 = s1;
        S2 = s2;

        //calculate vectors
        calculateNormals();
    }

    public void calculateNormals() {
        N0 = calculateNormal(S2, S0);
        N1 = calculateNormal(S0, S1);
        N2 = calculateNormal(S1, S2);
    }


    private Vector calculateNormal(Segment s0, Segment s1) {
        //origin is same point
        Vector v0;
        Vector v1;
        if (s0.P0 == s1.P0) {
            v0 = s0.vectorize();
            v1 = s1.vectorize();
        } else if (s0.P1 == s1.P1) {
            v0 = s0.vectorize().product(-1);
            v1 = s1.vectorize().product(-1);
        } else if (s0.P0 == s1.P1) {
            v0 = s0.vectorize();
            v1 = s1.vectorize().product(-1);
        } else if (s0.P1 == s1.P0) {
            v0 = s0.vectorize().product(-1);
            v1 = s1.vectorize();
        } else {
            return Vector.EmptyVector();
        }

        Vector cross = v0.product(v1);
        cross.normalize();
        return cross;
    }

}
