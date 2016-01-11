package me.alpha12.ecarnet.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowAnimationFrameStats;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.alpha12.ecarnet.R;
import me.alpha12.ecarnet.activities.MainActivity;
import me.alpha12.ecarnet.charts.LineChartCustom;
import me.alpha12.ecarnet.database.EcarnetHelper;
import me.alpha12.ecarnet.models.Car;
import me.alpha12.ecarnet.models.Intervention;
import me.alpha12.ecarnet.models.Operation;

/**
 * Created by guilhem on 09/01/2016.
 */
public class ChartDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {


    private Date limit;
    private ArrayList<Intervention>interventionsUpToDate;
    private ArrayList<Intervention>interventions;
    private LineChartCustom kilometersLineCustom;
    private LineChart kilometersLine;
    private Car currentCar;

    private final  ArrayList<String> durations = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_DialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_chart_kilometers, container, false);
        getDialog().getWindow().getAttributes().windowAnimations = WindowAnimationFrameStats.PARCELABLE_WRITE_RETURN_VALUE;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        // Set an OnMenuItemClickListener to handle menu item clicks
        ImageButton closeButton = (ImageButton) view.findViewById(R.id.closeIcon);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.durations.add("6 dernier mois");
        this.durations.add("12 derniers mois");
        this.durations.add("Tous");
        currentCar = ((MainActivity) getActivity()).currentCar;
        this.interventions = Intervention.getAllIntervention(EcarnetHelper.bdd, currentCar.getUuid());
        this.interventionsUpToDate = new ArrayList<>();
        Spinner durationSelector = (Spinner) view.findViewById(R.id.positionSelector);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, durations);
        durationSelector.setAdapter(adapter);

        Calendar c = Calendar.getInstance();
        limit = c.getTime();
        limit.setMonth(limit.getMonth() - 6);
        durationSelector.setOnItemSelectedListener(this);

        kilometersLine = (LineChart) view.findViewById(R.id.kilometersChart);




        //fake data
        interventions.add(new Intervention(currentCar.getUuid(), 10123, 5, new Date(114, 11, 12), new ArrayList<Operation>(), 12.5));
        interventions.add(new Intervention(currentCar.getUuid(), 10223, 50.25, new java.util.Date(115, 0, 12), new ArrayList<Operation>(), 15.5));
        interventions.add(new Intervention(currentCar.getUuid(), 10456, 50.25, new java.util.Date(115, 2, 12), new ArrayList<Operation>(), 18.5));
        interventions.add(new Intervention(currentCar.getUuid(), 10710, 200.43, new java.util.Date(115, 3, 12), new ArrayList<Operation>(), 0));
        interventions.add(new Intervention(currentCar.getUuid(), 11070, 50.90, new java.util.Date(115, 3, 12), new ArrayList<Operation>(), 15));
        interventions.add(new Intervention(currentCar.getUuid(), 11340, 108.23, new java.util.Date(115, 4, 12), new ArrayList<Operation>(), 0));
        interventions.add(new Intervention(currentCar.getUuid(), 11390, 200, new java.util.Date(115, 5, 12), new ArrayList<Operation>(), 0));
        interventions.add(new Intervention(currentCar.getUuid(), 11701, 60.83, new java.util.Date(115, 6, 12), new ArrayList<Operation>(), 25.3));
        interventions.add(new Intervention(currentCar.getUuid(), 11925, 120, new java.util.Date(115, 6, 12), new ArrayList<Operation>(), 0));
        interventions.add(new Intervention(currentCar.getUuid(), 12780, 100.33, new java.util.Date(115, 8, 12), new ArrayList<Operation>(), 0));
        interventions.add(new Intervention(currentCar.getUuid(), 13332, 130, new java.util.Date(115, 10, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 13782, 130, new java.util.Date(115, 11, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 14600, 130, new java.util.Date(116, 0, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 15770, 130, new java.util.Date(116, 0, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 15970, 130, new java.util.Date(116, 0, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 16500, 130, new java.util.Date(116, 0, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 16990, 130, new java.util.Date(116, 0, 12), new ArrayList<Operation>(), 45.3));
        interventions.add(new Intervention(currentCar.getUuid(), 16990, 130, new java.util.Date(116, 0, 12), new ArrayList<Operation>(), 45.3));






        updateChart();

        // Inflate a menu to be displayed in the toolbar
        return view;
    }


    public void updateChart()
    {
        getInterventionUpToDate();
        kilometersLineCustom = new LineChartCustom(this.kilometersLine, getEntriesChart(), "", getLabels(), null);
        kilometersLineCustom.getxAxis().setDrawLabels(true);
        kilometersLineCustom.getxAxis().setEnabled(true);
    }

    public ArrayList<Entry> getEntriesChart()
    {
        int i=0;
        int oldKilometers = currentCar.getKilometers();
        ArrayList<Entry> entries = new ArrayList<>();
        for(Intervention value : interventionsUpToDate)
        {
            entries.add(new Entry(value.getKilometers() - oldKilometers, i));
            i++;
            oldKilometers = value.getKilometers();
        }
        Log.d("number of entries", ""+entries.size());
        return entries;
    }

    public ArrayList<String> getLabels()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yy", Locale.FRENCH);
        ArrayList<String> labs = new ArrayList<>();
        for(Intervention value : interventionsUpToDate)
        {
                String dateOutput = sdf.format(value.getDateIntervention());
                labs.add(dateOutput);
        }
        Log.d("number of labels", ""+labs.size());
        return labs;
    }


    public void getInterventionUpToDate()
    {
        this.interventionsUpToDate.clear();
        for(Intervention value : this.interventions)
        {
            Log.d("date of intervention", "" + value.getDateIntervention().toString());
            Log.d("limit date", "" + limit.toString());
            if(value.getDateIntervention().after(limit))
            this.interventionsUpToDate.add(value);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Calendar c = Calendar.getInstance();
        limit = c.getTime();
        switch (position)
        {
            case 0 :
                limit.setMonth(limit.getMonth()-6);
                break;
            case 1:
                limit.setYear(limit.getYear()-1);
                break;
            case 2:
                limit.setTime(0);
                break;
        }
        updateChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}