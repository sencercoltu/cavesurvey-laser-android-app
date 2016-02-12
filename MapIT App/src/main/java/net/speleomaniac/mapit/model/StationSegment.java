package net.speleomaniac.mapit.model;

/**
 * Created by Sencer Coltu on 7.9.2014.
 */
public class StationSegment {
    public StationCoordinate Station1;
    public StationCoordinate Station2;

    public StationSegment(){

    }

    public StationSegment(StationCoordinate s1, StationCoordinate s2) {
        Station1 = s1;
        Station2 = s2;
    }

}
