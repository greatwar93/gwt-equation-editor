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

import gwt.g2d.client.graphics.Color;
import gwt.g2d.client.graphics.Surface;
import gwt.g2d.client.graphics.TextAlign;
import gwt.g2d.client.graphics.TextBaseline;
import gwt.g2d.client.graphics.shapes.ShapeBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;
import org.formed.client.formula.Rectangle;
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

    public void drawDottedLine(int x1, int y1, int x2, int y2) {
        countLine++;
        surface.setStrokeStyle(new Color(220, 220, 220));
        surface.strokeShape(new ShapeBuilder().drawLineSegment(x1, y1, x2, y2).build());
        surface.setStrokeStyle(new Color(0, 0, 0));
    }

    public void fillRect(int x1, int y1, int x2, int y2, int r, int g, int b) {
        surface.setFillStyle(new Color(r, g, b));
        surface.fillShape(new ShapeBuilder().moveTo(x1, y1).drawLineTo(x2, y1).drawLineTo(x2, y2).drawLineTo(x1, y2).drawLineTo(x1, y1).build());
        surface.setFillStyle(new Color(0, 0, 0));
    }

    private void drawRect(int x1, int y1, int x2, int y2) {
        surface.strokeShape(new ShapeBuilder().moveTo(x1, y1).drawLineTo(x2, y1).drawLineTo(x2, y2).drawLineTo(x1, y2).drawLineTo(x1, y1).build());
    }

    public int getSmallerSize(int size) {
        return size * 3 / 4;
    }

    protected int autoCompletionY = 0;

    public void drawAutoCompletion() {
        autoCompletionY = 0;
        int size = autoFound.size();
        if (size <= 0) {
            return;
        }

        cursor.reMeasure(this);
        int x = cursor.getX();
        int maxWidth = 0;
        int y1 = cursor.getY() + cursor.getHeightDown();
        int y = y1;

        int i = 0;
        for (AutoCompletion auto : autoFound) {
            if (auto.isForNew()) {
                FormulaItem item = auto.getNewItem();

                String text = auto.getFindText() + " -> ";
                Metrics metrics = textMetrics(text, 20);
                Metrics metrics2 = item.measure(this, 20);

                maxWidth = Math.max(maxWidth, metrics.getWidth() + metrics2.getWidth());

                if (i == autoCompletionPos) {
                    item.setHighlight(255, 255, 0);
                    fillRect(x, y, x + metrics.getWidth(), y + Math.max(metrics.getHeight(), metrics2.getHeight()) + 2, 255, 255, 0);
                }else{
                    item.setHighlight(255, 255, 255);
                    fillRect(x, y, x + metrics.getWidth(), y + Math.max(metrics.getHeight(), metrics2.getHeight()) + 2, 255, 255, 255);
                }

                y += Math.max(metrics.getHeightUp(), metrics2.getHeightUp()) + 1;
                drawText(text, 20, x, y);
                item.draw(this, x + metrics.getWidth(), y, 20);
                item.highlightOff();
                y += Math.max(metrics.getHeightDown(), metrics2.getHeightDown()) + 1;

            } else {
                String text = auto.getFindText() + " -> " + auto.getShowText();
                Metrics metrics = textMetrics(text, 20);

                maxWidth = Math.max(maxWidth, metrics.getWidth());

                if (i == autoCompletionPos) {
                    fillRect(x, y, x + metrics.getWidth(), y + metrics.getHeight() + 2, 255, 255, 0);
                }else{
                    fillRect(x, y, x + metrics.getWidth(), y + metrics.getHeight() + 2, 255, 255, 255);
                }

                y += metrics.getHeightUp() + 1;
                drawText(text, 20, x, y);
                y += metrics.getHeightDown() + 1;
            }

            i++;
        }

        autoCompletionY = y;
        drawRect(x, y1, x + maxWidth, y);
    }

    protected int redrawing = 0;
    public void redraw() {
        redrawing++;
        preRedraw();

        countLine = 0;
        countText = 0;
        countMeasure = 0;
        surface.clear();
        surface.setTextAlign(TextAlign.LEFT).setTextBaseline(TextBaseline.MIDDLE);

        //surface.setFont(20 + "px serif");
        lastFontSize = 0;

        Date from = new Date();

        formula.invalidateMetrics();
        drawerMetrics = formula.drawAligned(this, 10, 10, 20, Align.TOP);

        if (isAutoCompletion) {
            drawAutoCompletion();
        }

        Date till = new Date();
        drawText((till.getTime() - from.getTime()) + "ms " + countLine + " " + countText + " " + countMeasure, 20, 0, 10);

        surface.strokeRectangle(9, 9, 2 + drawerMetrics.getWidth(), 2 + drawerMetrics.getHeight());

        postRedraw();

        if (redrawing <= 1) {
            Rectangle rect = findMaxRect();
            int width = rect.getX() + rect.getWidth() + 5;
            int height = Math.max(rect.getY() + rect.getHeight() + 5, autoCompletionY + 5);
            if (width > surface.getWidth() || height > surface.getHeight()) {
                surface.setWidth(width);
                surface.setHeight(height);
                redraw();
            } else {
                surface.setWidth(width);
                surface.setHeight(height);
                redraw();
            }
        }

        redrawing--;
    }

    @Override
    public void redrawCursor() {
        if (cursorHighlight != null) {
            surface.setStrokeStyle(new Color(220, 220, 220));
            surface.strokeShape(new ShapeBuilder().drawLineSegment(cursorHighlight.getX(), cursorHighlight.getY() - cursorHighlight.getHeightUp(), cursorHighlight.getX(), cursorHighlight.getY() + cursorHighlight.getHeightDown()).build());
            surface.setStrokeStyle(new Color(0, 0, 0));
        }

        if (cursor != null) {
            surface.strokeShape(new ShapeBuilder().drawLineSegment(cursor.getX(), cursor.getY() - cursor.getHeightUp(), cursor.getX(), cursor.getY() + cursor.getHeightDown()).build());
        }
    }
}
