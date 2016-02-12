package net.speleomaniac.mapit.model;

/**
 * Created by Sencer Coltu on 1.9.2014.
 */
public class Vector {
    public float X;
    public float Y;
    public float Z;

    public static final Vector NullVector = EmptyVector();

    public static Vector EmptyVector() {
        return new Vector(0, 0, 0);
    }

    public Vector(float x, float y, float z) {
        X = x;
        Y = y;
        Z = z;
    }

    public boolean Equals(Vector v) {
        return X == v.X && Y == v.Y && Z == v.Z;
    }

    public Vector add(Vector v) {
        return new Vector(v.X + X, v.Y + Y, v.Z + Z);
    }

    public Vector subtract(Vector v) {
        return new Vector(X - v.X, Y - v.Y, Z - v.Z);
    }

    public Point add(Point p) {
        return new Point(p.X + X, p.Y + Y, p.Z + Z);
    }

    public Vector product(float a) {
        return new Vector(X*a, Y*a, Z*a);
    }

    public Vector product(Vector v) {
        return new Vector(Y*v.Z - Z*v.Y, Z*v.X - X*v.Z, X*v.Y - Y*v.X);
    }

    public float dot(Vector v) {
        return X*v.X + Y*v.Y + Z*v.Z;
    }

    public float length() {
        return (float) Math.sqrt(X*X + Y*Y + Z*Z);
    }

    public Vector normalize() {
        float l = length();
        X /= l;
        Y /= l;
        Z /= l;
        return this;
    }
}
