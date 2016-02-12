package net.speleomaniac.mapit.model;

/**
 * Created by Sencer Coltu on 7.9.2014.
 */
public class StationCoordinate {
    public float X;
    public float Y;
    public float Z;

    public StationCoordinate() {

    }

    public StationCoordinate(float x, float y, float z) {
        X = x;
        Y = y;
        Z = z;
    }

    public static final StationCoordinate NullCoordinate = EmptyCoordinate();

    public static StationCoordinate EmptyCoordinate() {
        return new StationCoordinate(0, 0, 0);
    }
}
