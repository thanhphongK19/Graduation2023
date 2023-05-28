package com.example.application;

import static java.util.logging.Logger.global;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.DataSensor1Binding;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import Database.zone1Database;

public class DataSensor1 extends AppCompatActivity implements OnChartValueSelectedListener {
    DataSensor1Binding binding;
    private CombinedChart mChart;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataSensor1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar  = getSupportActionBar();
        actionBar.setTitle("Thông số môi trường");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mChart = (CombinedChart) findViewById(R.id.combinedChart);
//        binding.buttonZone1.setBackgroundColor(Color.GREEN);
//        binding.buttonZone2.setBackgroundColor(Color.RED);
//
        binding.buttonZone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refresh = new Intent(DataSensor1.this,DataSensor1.class);
                startActivity(refresh);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        binding.buttonZone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataSensor1.this,DataSensor2.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });

        int data2 = zone1Database.getInstance(DataSensor1.this).zone1DAO().getNewestSoilTemperature();
        String dataShow2 = data2 + "℃";
        binding.soilTemperature1.setText(dataShow2);


        int data1 = zone1Database.getInstance(DataSensor1.this).zone1DAO().getNewestSoilMoisture();
        String dataShow1 = data1 + "%";
        binding.soilMoisture1.setText(dataShow1);

        int data3 = zone1Database.getInstance(DataSensor1.this).zone1DAO().getNewestAirTemperature();
        String dataShow3 = data3 + "℃";
        binding.airTemperature1.setText(dataShow3);

        int data4 = zone1Database.getInstance(DataSensor1.this).zone1DAO().getNewestAirHumidity();
        String dataShow4 = data4 + "%";
        binding.airHumidity1.setText(dataShow4);


        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.BLUE);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.RED);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        final String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat","Sun"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart());
        lineDatas.addDataSet((ILineDataSet) dataChart1());

        data.setData(lineDatas);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
    }


    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, "Giá trị: "
                + e.getY()
                + ", ngày: "
                + h.getX()
                , Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected() {

    }

    private static DataSet dataChart() {

        LineData d = new LineData();
        int[] data = new int[] { 27, 28, 29, 32, 31, 30, 34 };

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 7; index++) {
            entries.add(new Entry(index, data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Nhiệt độ đất");
        set.setColor(Color.RED);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.RED);
        set.setCircleRadius(5f);
        set.setFillColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.RED);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }

    private static LineDataSet dataChart1() {

        LineData d = new LineData();
        int[] data = new int[] { 45, 47, 48, 44, 49, 50, 46 };

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 7; index++) {
            entries.add(new Entry(index, data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Độ ẩm đất");
        set.setColor(Color.BLUE);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.BLUE);
        set.setCircleRadius(5f);
        set.setFillColor(Color.BLUE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.BLUE);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
