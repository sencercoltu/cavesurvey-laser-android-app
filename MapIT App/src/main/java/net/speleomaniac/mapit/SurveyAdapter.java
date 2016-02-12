package net.speleomaniac.mapit;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Sencer Coltu on 08.02.2014.
 */
public class SurveyAdapter extends ArrayAdapter<StationData> {
    //private final ArrayList<StationData> Stations;


    private final Context mContext;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public SurveyAdapter(Context context, ArrayList<StationData> stations) {
        //super(context, R.layout.listitem_survey);
        super(context, 0, stations);

        //dummy data ekle
        //Stations = stations;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_survey, parent, false);
            if (convertView == null) return null;
        }


        TextView _from = (TextView) convertView.findViewById(R.id.survey_data_from);
        TextView _to = (TextView) convertView.findViewById(R.id.survey_data_to);
        TextView _distance = (TextView) convertView.findViewById(R.id.survey_data_distance);
        TextView _bearing = (TextView) convertView.findViewById(R.id.survey_data_bearing);
        TextView _inclination = (TextView) convertView.findViewById(R.id.survey_data_inclination);
        TextView _barometer = (TextView) convertView.findViewById(R.id.survey_data_barometer);
        TextView _temperature = (TextView) convertView.findViewById(R.id.survey_data_temperature);
        TextView _type = (TextView) convertView.findViewById(R.id.survey_data_type);
        TextView _time = (TextView) convertView.findViewById(R.id.survey_data_time);



        StationData data = getItem(position);
            if (MapItActivity.SelectedStationAnchor == 0) {
                _to.setBackgroundColor(Color.TRANSPARENT);
                _to.setTextColor(Color.WHITE);
                if (data == MapItActivity.SelectedStation) {
                    _from.setTextColor(Color.BLACK);
                    _from.setBackgroundColor(Color.YELLOW);
                }
                else {
                    _from.setTextColor(Color.WHITE);
                    _from.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            else {
                _from.setBackgroundColor(Color.TRANSPARENT);
                _from.setTextColor(Color.WHITE);
                if (data == MapItActivity.SelectedStation) {
                    _to.setTextColor(Color.BLACK);
                    _to.setBackgroundColor(Color.YELLOW);
                }
                else {
                    _to.setTextColor(Color.WHITE);
                    _to.setBackgroundColor(Color.TRANSPARENT);
                }
            }

        _from.setText(data.From); _from.setGravity(Gravity.START);
        _to.setText(data.To); _to.setGravity(Gravity.START);
        _distance.setText(String.format("%.3f", data.Distance)); _distance.setGravity(Gravity.END);
        _bearing.setText(String.format("%.2f", data.Bearing)); _bearing.setGravity(Gravity.END);
        _inclination.setText(String.format("%.2f", data.Inclination)); _inclination.setGravity(Gravity.END);
        _barometer.setText(String.format("%.0f", data.Pressure)); _barometer.setGravity(Gravity.END);
        _temperature.setText(String.format("%.0f", data.Temperature)); _temperature.setGravity(Gravity.END);
        _type.setText("" + data.Type); _type.setGravity(Gravity.END);
        _time.setText(dateFormat.format(data.TimeStamp)); _time.setGravity(Gravity.END);


        _from.setLayoutParams(MapItActivity.Instance.headerFrom.getLayoutParams());
        _to.setLayoutParams(MapItActivity.Instance.headerTo.getLayoutParams());
        _distance.setLayoutParams(MapItActivity.Instance.headerDist.getLayoutParams());
        _bearing.setLayoutParams(MapItActivity.Instance.headerComp.getLayoutParams());
        _inclination.setLayoutParams(MapItActivity.Instance.headerInc.getLayoutParams());
        _barometer.setLayoutParams(MapItActivity.Instance.headerBar.getLayoutParams());
        _temperature.setLayoutParams(MapItActivity.Instance.headerTemp.getLayoutParams());
        _type.setLayoutParams(MapItActivity.Instance.headerType.getLayoutParams());
        _time.setLayoutParams(MapItActivity.Instance.headerTime.getLayoutParams());

        _from.setOnClickListener(new StationClickListener(data) {
            @Override
            public void onClick(View view) {
                MapItActivity.selectStation(station, 0);
            }
        });

        _to.setOnClickListener(new StationClickListener(data) {
            @Override
            public void onClick(View view) {
                MapItActivity.selectStation(station, 1);
            }
        });

        return convertView;
    }
}
