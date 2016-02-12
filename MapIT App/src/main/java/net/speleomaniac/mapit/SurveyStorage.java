package net.speleomaniac.mapit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.speleomaniac.mapit.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SurveyStorage extends SQLiteOpenHelper {
    private final String TABLE_SURVEYS = "_surveys";
    private final String COL_SURVEYS_ID = "_id";
    private final String COL_SURVEYS_NAME = "_name";
    private final String COL_SURVEYS_DATE = "_date";

    private final String COL_SURVEY_ID = "_id";
    private final String COL_SURVEY_TIMESTAMP = "_timestamp";
    private final String COL_SURVEY_FROM = "_from";
    private final String COL_SURVEY_TO = "_to";
    private final String COL_SURVEY_TYPE = "_type";
    private final String COL_SURVEY_DISTANCE = "_distance";
    private final String COL_SURVEY_BEARING = "_bearing";
    private final String COL_SURVEY_INCLINATION = "_inclination";
    private final String COL_SURVEY_TEMPERATURE = "_temperature";
    private final String COL_SURVEY_PRESSURE = "_pressure";

    //public final SurveyAdapter mAdapter;
    private final Context Context;
    public String SurveyName = "";
    private final SQLiteDatabase Database;

    public final ArrayList<StationData> Stations;
    public final SurveyAdapter Adapter;
    public final Model CaveModel;


    public SurveyStorage(Context context, int version) {
        super(context, "MapIT_Surveys", null, version);
        Stations = new ArrayList<StationData>();
        CaveModel = new Model(Stations);
        Adapter = new SurveyAdapter(MapItActivity.Instance, Stations);
        Context = context;
        Database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //ilk create edilirken surveylerin listesini tutacak database'i oluştur
        String createSurveysTable = "CREATE TABLE " + TABLE_SURVEYS + " (" +
                COL_SURVEYS_ID + " integer primary key autoincrement," +
                COL_SURVEYS_NAME + " text not null)";
        sqLiteDatabase.execSQL(createSurveysTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        //şimdilik bişey yapma
    }

    public List<String> GetAvailableSurveys() {
        ArrayList<String> surveys = new ArrayList<String>();
        try
        {
            Cursor cursor = Database.query(TABLE_SURVEYS, null, null, null, null, null, COL_SURVEYS_ID);
            if (cursor != null)
            {
                cursor.moveToFirst();
                int colId = cursor.getColumnIndex(COL_SURVEYS_NAME);
                do {
                    surveys.add(cursor.getString(colId));
                } while(cursor.moveToNext());
                cursor.close();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return surveys;
    }

    public String createSurvey(String surveyName) {
        if (surveyName.length() == 0)
            return "Empty survey name";
        try
        {
            Cursor cursor = Database.rawQuery("select " + COL_SURVEYS_NAME + " from " + TABLE_SURVEYS + " where " + COL_SURVEYS_NAME + " = '"+ surveyName +"'", null);
            if(cursor != null) {
                if(cursor.getCount()>0) {
                    cursor.close();
                    return String.format("Survey %s already exists.", surveyName);
                }
                cursor.close();
            }

            //tablo yoksa yarat
            String tableName = "_" + surveyName.replace(' ', '_');

            Database.execSQL("CREATE TABLE " + tableName + "(" +
                    COL_SURVEY_ID + " integer primary key autoincrement," +
                    COL_SURVEY_TIMESTAMP + " integer not null," +
                    COL_SURVEY_FROM + " text not null, " +
                    COL_SURVEY_TO + " text not null, " +
                    COL_SURVEY_TYPE + " int not null, " +
                    COL_SURVEY_DISTANCE + " real not null, " +
                    COL_SURVEY_BEARING + " real not null, " +
                    COL_SURVEY_INCLINATION + " real not null, " +
                    COL_SURVEY_TEMPERATURE + " real not null, " +
                    COL_SURVEY_PRESSURE + " real not null)");



            ContentValues contentValues = new ContentValues();

            contentValues.clear();
            contentValues.put(COL_SURVEYS_NAME, surveyName);
            Database.insert(TABLE_SURVEYS, null, contentValues);

            return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return String.format("Error creating survey %s: %s", surveyName, ex.getMessage());
        }
    }

    public String deleteSurvey(String surveyName) {
        if (surveyName.length() == 0) return "Empty survey name";
        if (SurveyName.equals(surveyName))
        {
            SurveyName = "";
            Stations.clear();
            CaveModel.reset();
            Adapter.notifyDataSetChanged();
        }

        String tableName = "_" + surveyName.replace(' ', '_');
        try
        {
            Database.execSQL("DROP TABLE " + tableName);
            Database.delete(TABLE_SURVEYS, COL_SURVEYS_NAME + "=?", new String[]{surveyName});
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return String.format("Error deleting survey %s: %s", surveyName, ex.getMessage());
        }
    }

    public String loadSurvey(String surveyName) {
        if (surveyName.length() == 0) return "Empty survey name";
        SurveyName = surveyName;

        String tableName = "_" + surveyName.replace(' ', '_');
        CaveModel.reset();
        Stations.clear();

        try
        {
            Cursor cursor =  Database.query(tableName, null, null, null, null, null, COL_SURVEY_FROM);
            if (cursor != null)
            {
                if (cursor.moveToFirst()) {
                    int id = cursor.getColumnIndex(COL_SURVEY_ID);
                    int timestamp = cursor.getColumnIndex(COL_SURVEY_TIMESTAMP);
                    int from = cursor.getColumnIndex(COL_SURVEY_FROM);
                    int to = cursor.getColumnIndex(COL_SURVEY_TO);
                    int type = cursor.getColumnIndex(COL_SURVEY_TYPE);
                    int distance = cursor.getColumnIndex(COL_SURVEY_DISTANCE);
                    int bearing = cursor.getColumnIndex(COL_SURVEY_BEARING);
                    int inclination = cursor.getColumnIndex(COL_SURVEY_INCLINATION);
                    int temperature = cursor.getColumnIndex(COL_SURVEY_TEMPERATURE);
                    int pressure = cursor.getColumnIndex(COL_SURVEY_PRESSURE);

                    do {
                        StationData sd = new StationData();
                        sd.Id = cursor.getLong(id);
                        sd.From = cursor.getString(from);
                        sd.To = cursor.getString(to);
                        sd.Type = (char) cursor.getInt(type);
                        sd.Distance = cursor.getFloat(distance);
                        sd.Bearing = cursor.getFloat(bearing);
                        sd.Inclination = cursor.getFloat(inclination);
                        sd.Temperature = cursor.getFloat(temperature);
                        sd.Pressure = cursor.getFloat(pressure);
                        sd.TimeStamp = new Date(cursor.getLong(timestamp));
                        Stations.add(sd);
                    }
                    while (cursor.moveToNext());
                }
                cursor.close();
                sortStations();
                setParents();
                calculateCoordinates();
                CaveModel.reset();
                CaveModel.refreshModel();
                Adapter.notifyDataSetChanged();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return String.format("Error loading survey %s: %s", surveyName, ex.getMessage());
        }
        return null;
    }

    private void sortStations() {
        Collections.sort(Stations, new Comparator<StationData>() {
            @Override
            public int compare(StationData d1, StationData d2) {
                String[] seg1 = d1.To.split("/");
                String[] seg2 = d2.To.split("/");
                int res = compareSegments(seg1, seg2);
                if (res > 0)
                    return 1;
                else if (res < 0)
                    return -1;
                else {
                    seg1 = d1.From.split("/");
                    seg2 = d2.From.split("/");
                    res = compareSegments(seg1, seg2);
                    if (res > 0)
                        return 1;
                    else if (res < 0)
                        return -1;
                }
                return 0;
            }
        });
    }

    private int compareSegments(String[] seg1, String[] seg2) {
        int cnt = Math.min(seg1.length, seg2.length);
        for (int i=0; i<cnt; i++) {
            String s1 = seg1[i];
            String s2 = seg2[i];
            int res = compareStation(s1, s2);
            if (res > 0)
                return 1;
            else if (res < 0)
                return -1;
        }
        return seg1.length - seg2.length;
    }

    private int compareStation(String s1, String s2) {
        String[] p1 = s1.split("\\.");
        String[] p2 = s2.split("\\.");
        int n1 = Integer.parseInt(p1[1]);
        int n2 = Integer.parseInt(p2[1]);
        return n1 - n2;
    }

    private String insertStation(StationData data) {
        if (SurveyName.length() == 0) return "No active survey";
        String tableName = "_" + SurveyName.replace(' ', '_');
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_SURVEY_TIMESTAMP, data.TimeStamp.getTime());
            contentValues.put(COL_SURVEY_FROM, data.From);
            contentValues.put(COL_SURVEY_TO, data.To);
            contentValues.put(COL_SURVEY_TYPE, (int) data.Type);
            contentValues.put(COL_SURVEY_DISTANCE, data.Distance);
            contentValues.put(COL_SURVEY_BEARING, data.Bearing);
            contentValues.put(COL_SURVEY_INCLINATION, data.Inclination);
            contentValues.put(COL_SURVEY_TEMPERATURE, data.Temperature);
            contentValues.put(COL_SURVEY_PRESSURE, data.Pressure);
            data.Id = Database.insert(tableName, null, contentValues);
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "Error inserting station: " + ex.getMessage();
        }
    }

    private String deleteStation(StationData data) {
        if (SurveyName.length() == 0) return "No active survey";
        String tableName = "_" + SurveyName.replace(' ', '_');
        try
        {
            Database.delete(tableName, COL_SURVEY_ID + "=?", new String[]{Long.toString(data.Id)});
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "Error deleting station: " + ex.getMessage();
        }
    }

    private StationData findStation(String stationName) {
        StationData s;
        for (int i=0; i< Stations.size(); i++) {
            s = Stations.get(i);
            if (s.To.equals(stationName))
                return s;
        }
        return null;
    }

    private void calculateCoordinates() {
        for (int i=0; i<Stations.size(); i++) {
            calculateCoordinate(Stations.get(i));
        }
    }

    private void calculateCoordinate(StationData data) {
        //calculate relative position to From station
        float l = (float)(data.Distance * Math.cos(Math.toRadians(data.Inclination)));
        data.Relative.X = l * (float) Math.sin(Math.toRadians(data.Bearing));
        data.Relative.Y = (float) (data.Distance * Math.sin(Math.toRadians(data.Inclination)));
        data.Relative.Z = l * (float) Math.cos(Math.toRadians(data.Bearing));

        if (data.Parent != null) {
            data.Absolute.X = data.Parent.Absolute.X + data.Relative.X;
            data.Absolute.Y = data.Parent.Absolute.Y + data.Relative.Y;
            data.Absolute.Z = data.Parent.Absolute.Z + data.Relative.Z;
        }
        else {
            data.Absolute.X = data.Relative.X;
            data.Absolute.Y = data.Relative.Y;
            data.Absolute.Z = data.Relative.Z;
        }
    }

    public String addStation(StationData data) {
        //otomatik isim verilecek

        //From: O.0 To: O.1 veya O.0/B1.1
        //From: O.0/B12.4 To:
        //From: O.1/B3.4 To: O.1/B3.5 veya O.1/B3.5/W1

        setParent(data);
        calculateCoordinate(data);

        boolean dataChanged = false;
        StationData s;
        String segment;
        //tipine göre son değeri bul
        switch (data.Type) {
            case StationData._wallShot: {
                int maxWall = 0;
                segment = data.From + "/";
                //branchlarda bak W ile başlayan var mı diye, en büyük olanı W12.1 i W13.1 yap
                for (int i = 0; i < Stations.size(); i++) {
                    s = Stations.get(i);
                    if (s.Type == StationData._wallShot && s.To.startsWith(segment)) {
                        String[] parts = getStationName(s.To).split("\\."); //W1.1
                        int no = Integer.parseInt(parts[0].substring(1));
                        if (no > maxWall) maxWall = no;
                    }
                }
                maxWall++;
                String name = "W" + maxWall + ".1";
                data.To = segment + name;
            }
            break;
            case StationData._mainShot: {
                //mainshot çakışıyosa eğer brancha çevir
                //parentin branchında aynı From'dan mainshot varsa branchshota çevir
                if (isAlreadyLinked(data.From)) {
                    data.Type = StationData._branchShot;
                    //break etmniyoruz brandchtan devam etsin diye
                } else {
                    //parent'in from'unu bir arttır
                    String[] segments = data.From.split("/");
                    String[] parts = segments[segments.length - 1].split("\\.");
                    int no = Integer.parseInt(parts[1]) + 1;
                    String parentSegmentName = getSegmentName(data.From);
                    if (parentSegmentName.length() > 0)
                        parentSegmentName += "/";
                    parentSegmentName += parts[0] + "." + no;
                    data.To = parentSegmentName;
                    break;
                }
            }
            case StationData._branchShot: {
                //en büyük branç ismini bul
                int maxBranch = 0;
                if (data.From.contains("/"))
                    segment = data.From + "/";
                else
                    segment = data.From;

                for (int i = 0; i < Stations.size(); i++) {
                    s = Stations.get(i);
                    if (s.Type == StationData._branchShot && s.To.startsWith(segment)) {
                        String stationName = getStationName(s.To);
                        String[] parts = stationName.split("\\."); //B2.1
                        int no = Integer.parseInt(parts[0].substring(1));
                        if (no > maxBranch) maxBranch = no;
                    }
                }
                maxBranch++;
                data.To = data.From + "/B" + maxBranch + ".1";
            }
            break;
        }

        Stations.add(data);
        sortStations();
        Adapter.notifyDataSetChanged();

        CaveModel.refreshModel();

        //add to database
        return insertStation(data);

    }

    public void removeStation(StationData data) {
        deleteStation(data);
        if (Stations.remove(data))
            Adapter.notifyDataSetChanged();
    }

    private boolean isAlreadyLinked(String from) {
        boolean ret = false;
        StationData s;
        for (int i=0; i< Stations.size(); i++) {
            s = Stations.get(i);
            if (s.Type != StationData._wallShot && s.From.equals(from))
                return true;
        }
        return false;
    }

    private String getStationName(String fullPath) {
        String[] segments = fullPath.split("/");
        return segments[segments.length - 1];
    }

    private String getSegmentName(String fullPath) {
        String[] segments = fullPath.split("/");
        return joinSegmentNames(segments, '/', segments.length - 1);
    }

    private String joinSegmentNames(String[] segments, char delimiter, int count) {
        String stationBase = "";
        for (int i=0; i<count; i++)
            stationBase += segments[i] + delimiter;
        return stationBase.replaceAll("/$", "");
    }

    private void setParents() {
        for (int i=0; i< Stations.size(); i++)
            setParent(Stations.get(i));
    }

    public void setParent(StationData data) {
        data.Parent = findStation(data.From);
    }
}
