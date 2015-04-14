/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.araploxio;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.widget.LinearLayout;

public class Chart {
    private static final int SCROLLBACK_SIZE = 400;

    private int sampleCount = 0;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;
    private GraphicalView mChart;

    public Chart(Context context, LinearLayout layout, String name, int color) {
        mDataset = new XYMultipleSeriesDataset();

        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setAxisTitleTextSize(0);
        mRenderer.setChartTitleTextSize(0);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setPointSize(1.0f);
        mRenderer.setMargins(new int[] { 10, 30, 30, 30 });
        mRenderer.setXAxisMin(0);
        mRenderer.setXAxisMax(1000);
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(1);
        mRenderer.setAxesColor(Color.WHITE);
        mRenderer.setLabelsColor(Color.WHITE);
        mRenderer.setLegendHeight(0);
        mRenderer.setShowLegend(false);
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(2);
        mRenderer.setShowGrid(false);
        mRenderer.setXLabelsAlign(Align.CENTER);
        mRenderer.setYLabelsAlign(Align.LEFT);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setMarginsColor(Color.BLACK);
        mRenderer.setAntialiasing(false);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);

        mCurrentSeries = new XYSeries(name);
        mDataset.addSeries(mCurrentSeries);

        mCurrentRenderer = new XYSeriesRenderer();
        mCurrentRenderer.setColor(color);
        mRenderer.addSeriesRenderer(mCurrentRenderer);

        mChart = ChartFactory.getLineChartView(context, mDataset, mRenderer);
        layout.addView(mChart);
    }

    public void add(int[] y) {
        for (int val : y) {
            int x = sampleCount++;
            mCurrentSeries.add(x, val);
            mRenderer.setXAxisMax(x);
            mRenderer.setXAxisMin(x - SCROLLBACK_SIZE + 1);
        }

        if (mCurrentSeries.getItemCount() > SCROLLBACK_SIZE)
            mCurrentSeries.remove(0);

        // Autoscale
        double maxY = mCurrentSeries.getMaxY();
        double minY = mCurrentSeries.getMinY();

        double diffY = maxY - minY;
        double avgY = (maxY + minY) / 2.0;
        if (diffY < 4000.0)
            diffY = 4000.0;
        maxY = avgY + diffY * 0.625;
        minY = avgY - diffY * 0.625;

        mRenderer.setYAxisMax(maxY);
        mRenderer.setYAxisMin(minY);

        mChart.repaint();
    }

    public void clear() {
        mCurrentSeries.clear();
    }
}
