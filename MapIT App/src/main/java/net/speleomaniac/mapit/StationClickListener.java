package net.speleomaniac.mapit;

import android.view.View;

/**
 * Created by Sencer Coltu on 6.9.2014.
 */
public class StationClickListener implements View.OnClickListener {
    public StationData station;
    public StationClickListener(StationData data) {
        station = data;
    }

    @Override
    public void onClick(View view) {

    }
}
