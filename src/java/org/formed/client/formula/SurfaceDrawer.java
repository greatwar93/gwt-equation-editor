/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formed.client.formula;

import gwt.g2d.client.graphics.Surface;
import gwt.g2d.client.graphics.TextAlign;
import gwt.g2d.client.graphics.TextBaseline;
import gwt.g2d.client.graphics.shapes.ShapeBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.PoweredElement;
import org.formed.client.formula.elements.RightCloser;
import org.formed.client.formula.elements.SimpleElement;

/**
 *
 * @author bulats
 */
public class SurfaceDrawer implements FormulaDrawer {

    private final Surface surface;
    private final Formula formula;
    private Cursor cursor = new Cursor(this, new SimpleElement(""), 0, 0, 0, 0, 0);
    private FormulaItem highlightedItem = new SimpleElement("");
    private final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
    private final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();

    public SurfaceDrawer(Surface surface, Formula formula) {
        /*
        canvas1.addCanvasPainter(new CanvasPainter() {

        public void drawCanvas(ICanvasExt canvas) {
        drawer.redraw();
        }
        });
         */

        this.surface = surface;
        this.formula = formula;
    }

    public void addDrawnItem(FormulaItem item, Rectangle rect) {
        items.put(item, rect);
    }

    public void addDrawnItem(FormulaItem item, int x, int y, Metrics metrics) {
        addDrawnItem(item, new Rectangle(x, y - metrics.getHeightUp(), metrics.getWidth(), metrics.getHeight()));
    }

    public void addDrawnFormula(Formula formula, Rectangle rect) {
        formulas.put(formula, rect);
    }

    public void addDrawnFormula(Formula formula, int x, int y, Metrics metrics) {
        addDrawnFormula(formula, new Rectangle(x, y - metrics.getHeightUp(), metrics.getWidth(), metrics.getHeight()));
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
    private Map<SizedText, Double> cachedMetrics = new HashMap<SizedText, Double>();

    public Metrics textMetrics(String text, int size) {
        /*                canvas1.setFontSize(size);
        TextMetrics metrics = canvas1.measureText(text);
        return new Metrics(metrics.getWidth(), metrics.getHeight() / 2, metrics.getHeight() / 2);*/

        Double cached = cachedMetrics.get(new SizedText(text, size));
        if (cached == null) {
            //We should measure text-height also
            cached = surface.setFont(size + "px serif").measureText(text);
            cachedMetrics.put(new SizedText(text, size), cached);
        }

        return new Metrics(cached, size / 2+2, size / 2-2);
//        return new Metrics(cached, size, 0);
//                return new Metrics(10, 10, 10);
    }

    public void drawText(String text, int size, int x, int y) {
        /*                canvas1.setFontSize(size);
        TextMetrics metrics = canvas1.measureText(text);
        canvas1.strokeText(text, (int) (x + metrics.getWidth()), (int) (y - metrics.getHeight() / 2));*/

        surface.setFont(size + "px serif").fillText(text, x, y);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        /*                canvas1.beginPath();
        canvas1.moveTo(x1, y1);
        canvas1.lineTo(x2, y2);
        canvas1.stroke();*/

        surface.strokeShape(new ShapeBuilder().drawLineSegment(x1, y1, x2, y2).build());
    }

    public int getSmallerSize(int size) {
        return size * 3 / 4;
    }

    public void redraw() {
        /*                canvas1.clear();

        canvas1.setAlign(net.kornr.abstractcanvas.client.gwt.CanvasPanelExt.ALIGN_END);
        canvas1.setFillStyle(Color.WHITE);
        canvas1.setGlobalAlpha(1.0);
        canvas1.fillRect(0, 0, canvas1.getCoordWidth(), canvas1.getCoordHeight());

        canvas1.setStrokeStyle(Color.BLACK);
        canvas1.setLineWidth(1);*/

        surface.clear();
        surface.setTextAlign(TextAlign.LEFT).setTextBaseline(TextBaseline.MIDDLE);

        Date from = new Date();
        Metrics metrics = formula.drawAligned(this, 10, 10, 20, FormulaDrawer.Align.TOP);
        Date till = new Date();
        drawText((till.getTime() - from.getTime()) + "ms", 20, 0, 10);

        /*
        canvas1.beginPath();
        canvas1.rect(9, 9, 2 + metrics.getWidth(), 2 + metrics.getHeight());
        canvas1.stroke();

        canvas1.beginPath();
        canvas1.moveTo(cursor.getX(), cursor.getY() - cursor.getHeightUp());
        canvas1.lineTo(cursor.getX(), cursor.getY() + cursor.getHeightDown());
        canvas1.stroke();*/

        surface.strokeRectangle(9, 9, 2 + metrics.getWidth(), 2 + metrics.getHeight());
        surface.strokeShape(new ShapeBuilder().drawLineSegment(cursor.getX(), cursor.getY() - cursor.getHeightUp(), cursor.getX(), cursor.getY() + cursor.getHeightDown()).build());
    }

    private boolean setCursor(Cursor newCursor) {
        if (newCursor == null) {
            return false;
        }

        cursor = newCursor;
        redraw();
        return true;
    }

    public boolean moveCursorUp() {
        return setCursor(cursor.getItem().getUp(this, cursor.getPosition()));
//                redraw();
//                return false;
    }

    public boolean moveCursorDown() {
        return setCursor(cursor.getItem().getDown(this, cursor.getPosition()));
//                moveCursorRight();
//                return false;
    }

    public boolean moveCursorLeft() {
        return setCursor(cursor.getItem().getLeft(this, cursor.getPosition()));
    }

    public boolean moveCursorRight() {
        return setCursor(cursor.getItem().getRight(this, cursor.getPosition()));
    }

    public void insert(char c) {
        FormulaItem item = null;
        switch (c) {
            case '+':
            case '-':
            case '*':
            case '/':
                item = new OperatorElement("" + c);
                cursor.getItem().getParent().insertAfter(item, cursor.getItem());
                setCursor(item.getLast(this));
                break;

            case '(':
                item = new LeftCloser();
                cursor.getItem().getParent().insertAfter(item, cursor.getItem());
                setCursor(item.getLast(this));
                break;
            case ')':
                item = new RightCloser();
                cursor.getItem().getParent().insertAfter(item, cursor.getItem());
                setCursor(item.getLast(this));
                break;

            case '^':
                if (cursor.getItem() instanceof PoweredElement) {
                    moveCursorUp();
                }
                break;

            default:
                setCursor(cursor.getItem().insertChar(this, cursor, c));
        }
        redraw();
    }

    public void deleteLeft(){

    }

    public void deleteRight(){
        
    }

    public FormulaItem selectItemAt(int x, int y) {
        Rectangle minRect = null;
        FormulaItem minRectItem = null;
        for (FormulaItem item : items.keySet()) {
            Rectangle rect = items.get(item);
            if (rect.isInside(x, y)) {
                if (rect.isSmaller(minRect)) {
                    minRectItem = item;
                    minRect = rect;
                }
            }
        }
        cursor = minRectItem.getCursor(this, x, y);
        redraw();

        return minRectItem;
    }

    public FormulaItem highlightItemAt(int x, int y) {
        Rectangle minRect = null;
        FormulaItem minRectItem = null;
        for (FormulaItem item : items.keySet()) {
            Rectangle rect = items.get(item);
            if (rect.isInside(x, y)) {
                if (rect.isSmaller(minRect)) {
                    minRectItem = item;
                    minRect = rect;
                }
            }
        }
        if (minRect != null) {
            highlightedItem = minRectItem;
            redraw();
            /*
            canvas1.setFillStyle(Color.DARK_BLUE);
            canvas1.fillRect(minRect.getX(), minRect.getY(), minRect.getWidth(), minRect.getHeight());*/
        }

        return minRectItem;
    }
}
