/*
Copyright 2010 Bulat Sirazetdinov
Copyright 2009 Bulat Sirazetdinov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.formed.client.formula.impl;

import gwt.g2d.client.graphics.Surface;
import gwt.g2d.client.graphics.TextAlign;
import gwt.g2d.client.graphics.TextBaseline;
import gwt.g2d.client.graphics.shapes.ShapeBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.formed.client.formula.Formula;
import org.formed.client.formula.Metrics;
import org.formed.client.formula.Undoer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class SurfaceDrawer extends BaseDrawer {

    private final Surface surface;
    private Map<SizedText, Double> cachedMetrics = new HashMap<SizedText, Double>(); //Text metrics cache
    private int lastFontSize = 20; //Last used font to minimize font switching

    private int countLine = 0;
    private int countText = 0;
    private int countMeasure = 0;

    public SurfaceDrawer(Surface surface, Formula formula) {
        super(formula);

        this.surface = surface;
    }

    public SurfaceDrawer(Surface surface, Undoer undoer, Formula formula) {
        super(formula, undoer);

        this.surface = surface;
    }

    private class SizedText {

        private String text;
        private int size;

        public SizedText(String text, int size) {
            this.text = text;
            this.size = size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SizedText other = (SizedText) obj;
            if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
                return false;
            }
            if (this.size != other.size) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + (this.text != null ? this.text.hashCode() : 0);
            hash = 47 * hash + this.size;
            return hash;
        }
    }

    public Metrics textMetrics(String text, int size) {
        countMeasure++;
        Double cached = cachedMetrics.get(new SizedText(text, size));
        if (cached == null) {
            //We should measure text-height also
            cached = surface.setFont(size + "px serif").measureText(text);
            cachedMetrics.put(new SizedText(text, size), cached);
        }

        return new Metrics(cached, size / 2 + 2, size / 2 - 2);
//        return new Metrics(cached, size, 0);
//                return new Metrics(10, 10, 10);
    }
    
    public void drawText(String text, int size, int x, int y) {
        countText++;
        if (size != lastFontSize) {
            surface.setFont(size + "px serif");
            lastFontSize = size;
        }
        surface.fillText(text, x, y);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        countLine++;
        surface.strokeShape(new ShapeBuilder().drawLineSegment(x1, y1, x2, y2).build());
    }

    public int getSmallerSize(int size) {
        return size * 3 / 4;
    }

    public void redraw() {
        countLine = 0;
        countText = 0;
        countMeasure = 0;
        surface.clear();
        surface.setTextAlign(TextAlign.LEFT).setTextBaseline(TextBaseline.MIDDLE);

        surface.setFont("20px serif");
        lastFontSize = 20;

        Date from = new Date();
        formula.invalidateMetrics();
        drawerMetrics = formula.drawAligned(this, 10, 10, 20, Align.TOP);
        Date till = new Date();
        drawText((till.getTime() - from.getTime()) + "ms " + countLine + " " + countText + " " + countMeasure, 20, 0, 10);

        surface.strokeRectangle(9, 9, 2 + drawerMetrics.getWidth(), 2 + drawerMetrics.getHeight());
        cursor.reMeasure(this);
        redrawCursor();
    }

    @Override
    public void redrawCursor() {
        surface.strokeShape(new ShapeBuilder().drawLineSegment(cursor.getX(), cursor.getY() - cursor.getHeightUp(), cursor.getX(), cursor.getY() + cursor.getHeightDown()).build());
    }
}
