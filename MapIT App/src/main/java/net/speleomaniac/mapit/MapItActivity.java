package net.speleomaniac.mapit;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.speleomaniac.mapit.model.Ray;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapItActivity extends ActionBarActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener
{
    private View mMainView;
    private ActionBar mActionBar;
    private BroadcastReceiver mBroadcastReceiver;
    private long mLastBackPressed = 0;
    private int mBackPressCount = 0;
    private Toast mExitToast = null;

    public static SharedPreferences mSharedPrefs;
    public static SurveyStorage mStorage;
    public static MapItActivity Instance;

    private Spinner mShotSpinner;
    private Spinner mReverseSpinner;
    public SurveyGLSurfaceView mGLView;
    private ListView mSurveyList;
    public static StationData SelectedStation = null;
    public static int SelectedStationAnchor = 0;

    public String SurveyToLoad = null;

    public TextView headerFrom;
    public TextView headerTo;
    public TextView headerDist;
    public TextView headerComp;
    public TextView headerInc;
    public TextView headerBar;
    public TextView headerTemp;
    public TextView headerType;
    public TextView headerTime;

    private final StationData rootStation;

    public MapItActivity() {
        rootStation = new StationData();
        rootStation.From = "O.0";
        rootStation.To = "O.0";
        rootStation.Id = -1;
    }

    public static void selectStation(StationData data, int index) {
        SelectedStation = data;
        SelectedStationAnchor = index;
        mStorage.Adapter.notifyDataSetChanged();
        mStorage.CaveModel.selectSegment(data, index);
        Instance.mGLView.requestRender();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainView = findViewById(R.id.main_window);

        mSurveyList = (ListView) mMainView.findViewById(R.id.survey_list);
        mShotSpinner = (Spinner) mMainView.findViewById(R.id.shot_spinner);
        mReverseSpinner = (Spinner) mMainView.findViewById(R.id.reverse_spinner);
        mGLView = (SurveyGLSurfaceView) mMainView.findViewById(R.id.survey_view);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Instance = this;
        mStorage = new SurveyStorage(this, 1);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);

        headerFrom = (TextView) mMainView.findViewById(R.id.survey_data_from);
        headerTo = (TextView) mMainView.findViewById(R.id.survey_data_to);
        headerDist = (TextView) mMainView.findViewById(R.id.survey_data_distance);
        headerComp = (TextView) mMainView.findViewById(R.id.survey_data_bearing);
        headerInc = (TextView) mMainView.findViewById(R.id.survey_data_inclination);
        headerBar = (TextView) mMainView.findViewById(R.id.survey_data_barometer);
        headerTemp = (TextView) mMainView.findViewById(R.id.survey_data_temperature);
        headerType = (TextView) mMainView.findViewById(R.id.survey_data_type);
        headerTime = (TextView) mMainView.findViewById(R.id.survey_data_time);


        setScreenPreference();
        setServicePreference();
        setColumnPreference();

        mSurveyList.setAdapter(mStorage.Adapter);

        if (MapItActivity.Instance.SurveyToLoad != null)
            loadSurvey(MapItActivity.Instance.SurveyToLoad);
        MapItActivity.Instance.SurveyToLoad = null;
    }

    @Override
    protected void onDestroy() {
        if (mExitToast != null)
            mExitToast.cancel();
        stopExternalService();
        super.onDestroy();
        mStorage.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGLView.onResume();
        //loadSurvey();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StationData data = (StationData) parent.getItemAtPosition(position);
        SelectedStation = data;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        StationData data = (StationData) adapterView.getItemAtPosition(i);
        MapItActivity.mStorage.removeStation(data);
        if (SelectedStation == data) SelectedStation = null;
        return false;
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount == 0)
        {
            long backPressed = Calendar.getInstance().getTimeInMillis();
            if (backPressed - mLastBackPressed < 1000)
                mBackPressCount++;
            else
                mBackPressCount = 0;

            mLastBackPressed = backPressed;

            int n = 5 - mBackPressCount;
            if (n <= 0)
            {
                if (mExitToast != null)
                    mExitToast.cancel();
                mExitToast = null;
                finish();
                return;
            }

            if (mExitToast != null)
                mExitToast.cancel();
            mExitToast = Toast.makeText(this, "Press back " + n + " more time" + ((n != 1)?"s":"") + " to exit.", Toast.LENGTH_SHORT);
            mExitToast.show();
        }
        else
        {
            String closedFragment = getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1).getName();
            if (closedFragment.equals("Settings"))
            {
                setServicePreference();
                setScreenPreference();
                //getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mMainFragment).commit();
            }
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean checked = false;
        switch (item.getItemId()) {
            case R.id.load_survey: {
                List<String> surveys = MapItActivity.mStorage.GetAvailableSurveys();
                if (surveys.size() == 0) {
                    Toast.makeText(MapItActivity.Instance, "No surveys to list", Toast.LENGTH_SHORT).show();
                    return true;
                }

                View view = getLayoutInflater().inflate(R.layout.survey_load, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Load Survey")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setView(view);

                final AlertDialog loadDialog = builder.create();
                final ListView surveyList = (ListView) view.findViewById(R.id.survey_list);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapItActivity.Instance, R.layout.listitem_load, R.id.survey_name, surveys)
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final ArrayAdapter<String> adapter = this;
                        View view = super.getView(position, convertView, parent);
                        Button loadButton = (Button) view.findViewById(R.id.survey_load);
                        Button deleteButton = (Button) view.findViewById(R.id.survey_delete);
                        TextView surveyName = (TextView) view.findViewById(R.id.survey_name);
                        deleteButton.setTag(surveyName.getText());
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String surveyName = (String) view.getTag();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapItActivity.Instance);
                                builder.setTitle("Delete Survey");

                                final TextView message = new TextView(MapItActivity.Instance);
                                message.setText("Really delete survey " + surveyName + "?");
                                builder.setView(message);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MapItActivity.mStorage.deleteSurvey(surveyName);
                                        mGLView.requestRender();
                                        adapter.remove(surveyName);
                                        adapter.notifyDataSetChanged();
                                        loadDialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        });

                        loadButton.setTag(surveyName.getText());
                        loadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String surveyName = (String) view.getTag();
                                loadSurvey(surveyName);
                                mGLView.requestRender();
                                loadDialog.dismiss();
                            }
                        });
                        return view;
                    }
                };

                surveyList.setAdapter(adapter);

                loadDialog.show();
                break;

            }
            case R.id.new_survey: {
                View view = getLayoutInflater().inflate(R.layout.survey_new, null);
                final EditText surveyNameEdit = (EditText) view.findViewById(R.id.survey_name);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("New Survey")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String name = surveyNameEdit.getText().toString().trim();
                            if (name.isEmpty()) {
                                Toast.makeText(MapItActivity.Instance, "Name not entered", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String res = mStorage.createSurvey(name);
                            if (res != null) {
                                Toast.makeText(MapItActivity.Instance, res, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            loadSurvey(name);
                            dialog.dismiss();
                        }
                    })
                    .setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            }
            case R.id.external_service:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_input_method", checked).apply();
                setServicePreference();
                break;
            case R.id.extend_display:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_maximize", checked).apply();
                setScreenPreference();
                break;
            case R.id.hide_from:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidefrom", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_to:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hideto", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_dist:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidedist", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_comp:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidecomp", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_inc:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hideinc", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_bar:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidebar", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_temp:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidetemp", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_type:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidetype", checked).apply();
                setColumnPreference();
                break;
            case R.id.hide_time:
                checked = item.isChecked();
                checked = !checked;
                item.setChecked(checked);
                mSharedPrefs.edit().putBoolean("prefs_hidetime", checked).apply();
                setColumnPreference();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem settingsMenu = menu.findItem(R.id.settings_menu);
        MenuItem serviceMenu = settingsMenu.getSubMenu().findItem(R.id.external_service);
        MenuItem expandMenu = settingsMenu.getSubMenu().findItem(R.id.extend_display);

        MenuItem hideMenu = settingsMenu.getSubMenu().findItem(R.id.hide_columns);

        MenuItem hideFromMenu = hideMenu.getSubMenu().findItem(R.id.hide_from);
        MenuItem hideToMenu = hideMenu.getSubMenu().findItem(R.id.hide_to);
        MenuItem hideDistMenu = hideMenu.getSubMenu().findItem(R.id.hide_dist);
        MenuItem hideBearMenu = hideMenu.getSubMenu().findItem(R.id.hide_comp);
        MenuItem hideIncMenu = hideMenu.getSubMenu().findItem(R.id.hide_inc);
        MenuItem hideBarMenu = hideMenu.getSubMenu().findItem(R.id.hide_bar);
        MenuItem hideTempMenu = hideMenu.getSubMenu().findItem(R.id.hide_temp);
        MenuItem hideTypeMenu = hideMenu.getSubMenu().findItem(R.id.hide_type);
        MenuItem hideTimeMenu = hideMenu.getSubMenu().findItem(R.id.hide_time);


        serviceMenu.setChecked(mSharedPrefs.getBoolean("prefs_input_method", false));
        expandMenu.setChecked(mSharedPrefs.getBoolean("prefs_maximize", false));

        hideFromMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidefrom", false));
        hideToMenu.setChecked(mSharedPrefs.getBoolean("prefs_hideto", false));
        hideDistMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidedist", false));
        hideBearMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidecomp", false));
        hideIncMenu.setChecked(mSharedPrefs.getBoolean("prefs_hideinc", false));
        hideBarMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidebar", true));
        hideTempMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidetemp", true));
        hideTypeMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidetype", true));
        hideTimeMenu.setChecked(mSharedPrefs.getBoolean("prefs_hidetime", true));

        return super.onPrepareOptionsMenu(menu);
    }

    public void loadSurvey(String surveyName) {
        //if (MapItActivity.mStorage.SurveyName.equals(surveyName)) return;

        //MapItActivity.Instance.CaveModel.reset();
        SelectedStation = rootStation;
        SelectedStationAnchor = 1;

        MapItActivity.mStorage.loadSurvey(surveyName);
        MapItActivity.Instance.SetTitle(surveyName);

        if (MapItActivity.mStorage.Stations.size() > 0) {
            SelectedStation = MapItActivity.mStorage.Stations.get(MapItActivity.mStorage.Stations.size() - 1);
            mSurveyList.setSelection(mStorage.Stations.indexOf(SelectedStation));
        }

        Toast.makeText(this, "Survey '" + surveyName + "' loaded.", Toast.LENGTH_SHORT).show();
    }

    public void addStationData(float distance, float bearing, float inclination, float temperature, float pressure) {
        if (SelectedStation == null) {
            Toast.makeText(this, "Please select 'From' station before adding station", Toast.LENGTH_SHORT).show();
            return;
        }

        String shotType = (String) mShotSpinner.getSelectedItem();
        String reverseType = (String) mReverseSpinner.getSelectedItem();

        boolean isReverse = false;
        if (reverseType.charAt(0) == StationData._reverseShot)
            isReverse = true;

        StationData data = new StationData();
        data.TimeStamp = new Date();
        data.Type = shotType.charAt(0);
        data.From = SelectedStationAnchor == 0? SelectedStation.From : SelectedStation.To;
        data.Distance = distance;
        data.Bearing = (isReverse)? (bearing + 180.0f) % 360.0f : bearing;
        data.Inclination = (isReverse)? -inclination: inclination;
        data.Temperature = temperature;
        data.Pressure = pressure;

        String result = MapItActivity.mStorage.addStation(data);
        if (result != null)
            Toast.makeText(this, "Error inserting row: " + result, Toast.LENGTH_SHORT).show();
        else {
            if (data.Type == StationData._mainShot || data.Type == StationData._branchShot) {
                SelectedStation = data;
                SelectedStationAnchor = 1;
            }
            mSurveyList.setSelection(mStorage.Stations.indexOf(data));
            mStorage.CaveModel.selectSegment(SelectedStation, SelectedStationAnchor);
        }
        mGLView.requestRender();
    }

    public static void SetTitle(String name)
    {
        Instance.mActionBar.setTitle("MapIT - " + name);
    }

    private void startExternalService() {
        if (mBroadcastReceiver == null)
        {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    Bundle extras = intent.getExtras();
                    if (extras.containsKey("DATA"))
                        processExternalData(extras.getString("DATA"));
                }
            };
            IntentFilter intentFilter = new IntentFilter(getResources().getString(R.string.device_data));
            registerReceiver(mBroadcastReceiver, intentFilter);
        }

        Intent intent = new Intent(getResources().getString(R.string.device_dataservice));
        intent.setPackage("net.speleomaniac.mapit.sencemeterservice");
        startService(intent);
    }

    private void stopExternalService() {
        if (mBroadcastReceiver != null)
        {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
            Intent intent = new Intent(getResources().getString(R.string.device_dataservice));
            intent.setPackage("net.speleomaniac.mapit.sencemeterservice");
            stopService(intent);
        }
    }

    private void setScreenPreference() {
        boolean maximizeScreen = mSharedPrefs.getBoolean("prefs_maximize", false);
        if (maximizeScreen)
        {
            mActionBar.hide();
            mMainView.setSystemUiVisibility(8);
        }
        else
        {
            mActionBar.show();
            mMainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    private void setServicePreference() {
        boolean useExternalService = mSharedPrefs.getBoolean("prefs_input_method", false);
        if (!useExternalService)
            stopExternalService();
        else
            startExternalService();
    }

    private void setColumnPreference() {
        setColumnPreference(headerFrom, "prefs_hidefrom", false);
        setColumnPreference(headerTo, "prefs_hideto", false);
        setColumnPreference(headerDist, "prefs_hidedist", false);
        setColumnPreference(headerComp, "prefs_hidecomp", false);
        setColumnPreference(headerInc, "prefs_hideinc", false);
        setColumnPreference(headerBar, "prefs_hidebar", true);
        setColumnPreference(headerTemp, "prefs_hidetemp", true);
        setColumnPreference(headerType, "prefs_hidetype", true);
        setColumnPreference(headerTime, "prefs_hidetime", true);

        mStorage.Adapter.notifyDataSetChanged();
    }

    private void setColumnPreference(TextView column, String preference, boolean def) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) column.getLayoutParams();
        params.weight = mSharedPrefs.getBoolean(preference, def) ? 0f : 1f;
        column.setLayoutParams(params);
    }

    private void processExternalData(String data) {
        String[] fields = data.split(";");
        float length = 0;
        float yaw = 0;
        float pitch = 0;
        float temperature = 0;
        float pressure = 0;
        for(String field : fields)
        {
            String[] parts = field.split("=");
            if (parts.length != 2) continue;
            int idx = Integer.parseInt(parts[0]);
            float value= Float.parseFloat(parts[1]);
            switch(idx)
            {
                case 1:
                    length = value;
                    break;
                case 2:
                    yaw = value;
                    break;
                case 3:
                    pitch = value;
                    break;
                case 5:
                    temperature = value;
                    break;
                case 6:
                    pressure = value;
                    break;
            }
        }

        addStationData(length, yaw, pitch, temperature, pressure);
        //CaveModel.refreshModel();
    }

    public void insertRay(Ray r) {
        //CaveModel.addRay(r);
    }
}


