package net.speleomaniac.mapit;

import net.speleomaniac.mapit.model.StationCoordinate;

import java.util.Date;

/**
 * Created by Sencer Coltu on 04.02.2014.
 */
public class StationData {

    public final static char _mainShot = 'M';
    public final static char _wallShot = 'W';
    public final static char _branchShot = 'B';
    public final static char _reverseShot = 'R';

    public StationData Parent;
    public char Type;
    public long Id;
    public Date TimeStamp;
    public String From;
    public String To;
    public float Distance;
    public float Bearing;
    public float Inclination;
    public float Temperature;
    public float Pressure;

    public StationCoordinate Relative = new StationCoordinate();
    public StationCoordinate Absolute = new StationCoordinate();
}
