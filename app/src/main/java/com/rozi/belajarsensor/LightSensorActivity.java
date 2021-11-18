package com.rozi.belajarsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

public class LightSensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView txtBrighnessInfo;
    private MediaPlayer mPlayer;
    private GraphView mGraphLight;
    private LineGraphSeries<DataPoint> mSeriesLight;
    private double graphLastAccelXValue = 5d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);

        txtBrighnessInfo = findViewById(R.id.txt_brightness_info);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null){
            txtBrighnessInfo.setText("Sensor tidak tersedia");
        }else{
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        mGraphLight = initGraph(R.id.graph_light, "Sensor of Light");
        mSeriesLight = initSeries(Color.RED, "Lux");
        mGraphLight.addSeries(mSeriesLight);
    }

    public GraphView initGraph(int id, String tittle){
        GraphView graphView = (GraphView) findViewById(id);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(5);
        graphView.getGridLabelRenderer().setLabelVerticalWidth(100);
        graphView.setTitle(tittle);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        return graphView;
    }
    public LineGraphSeries<DataPoint> initSeries(int color, String tittle){
        LineGraphSeries<DataPoint> series;
        series = new LineGraphSeries<>();
        series.setDrawDataPoints(true);
        series.setDrawBackground(true);
        series.setColor(color);
        series.setTitle(tittle);
        series.setBackgroundColor(Color.argb(100, 204, 119, 119));
        return series;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType){
            case Sensor.TYPE_LIGHT:
                txtBrighnessInfo.setText(getResources().getString(R.string.label_brighness, sensorEvent.values[0]));
                graphLastAccelXValue += 0.15d;
                mSeriesLight.appendData(new DataPoint(graphLastAccelXValue, sensorEvent.values[0]),
                        true, 33);
                if (sensorEvent.values[0] == 0){
                    mPlayer = new MediaPlayer();
                    try {
                        AssetFileDescriptor as = this.getAssets().openFd("cahaya_gelap.mp3");
                        mPlayer.setDataSource(as.getFileDescriptor(), as.getStartOffset(), as.getLength());
                        as.close();
                        mPlayer.prepare();
                        mPlayer.start();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else if(sensorEvent.values[0] > 180){
                    mPlayer = new MediaPlayer();

                    try {
                        AssetFileDescriptor as = this.getAssets().openFd("cahaya_terang.mp3");
                        mPlayer.setDataSource(as.getFileDescriptor(), as.getStartOffset(), as.getLength());
                        as.close();
                        mPlayer.prepare();
                        mPlayer.start();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sensorManager.unregisterListener(this, lightSensor);
    }
}