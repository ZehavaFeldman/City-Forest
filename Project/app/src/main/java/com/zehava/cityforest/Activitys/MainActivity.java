package com.zehava.cityforest.Activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.numetriclabz.numandroidcharts.ChartData;
import com.numetriclabz.numandroidcharts.ScatterChart;
import com.zehava.cityforest.R;
import android.graphics.Color;
import android.os.Bundle;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScatterChart chart = (ScatterChart) findViewById(R.id.chart);


        List<ChartData> values = new ArrayList<>();
                values.add(new ChartData(2f, 0.5f));  //values.add(new ChartData(y,x));<br />
                values.add(new ChartData(2f, 1f));
                values.add(new ChartData(2f, 2f));
                values.add(new ChartData(2f, 4f));
                values.add(new ChartData(2f, 5f));


        chart.setData(values);
        chart.setDescription("My Chart");
        chart.setHorizontal_label(getXAxisValues());
        chart.setGesture(true);
        chart.setCircleSize(15f);
        chart.invalidate();
    }



    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }
}
