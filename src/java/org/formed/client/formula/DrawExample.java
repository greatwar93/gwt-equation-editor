/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formed.client.formula;

import org.formed.client.formula.elements.SimpleElement;
import org.formed.client.formula.elements.RootElement;
import org.formed.client.formula.elements.FunctionElement;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.DivisorElement;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import java.util.HashMap;
import java.util.Map;
import net.kornr.abstractcanvas.client.CanvasPainter;
import net.kornr.abstractcanvas.client.ICanvasExt;
import net.kornr.abstractcanvas.client.TextMetrics;
import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.PoweredElement;
import org.formed.client.formula.elements.RightCloser;

/**
 *
 * @author bulats
 */
public class DrawExample {

    public void draw() {
        final Formula formula = new Formula();

        formula.add(new SimpleElement("S"));
        formula.add(new OperatorElement("="));
        formula.add(new SimpleElement("a", new Formula().add(new SimpleElement("2"))));
        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha"))));
        formula.add(new OperatorElement("-"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a")).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta"))));
        formula.add(new OperatorElement("+"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a", new Formula().add(new RootElement(new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("alpha")).add(new SimpleElement("b", new Formula().add(new SimpleElement("2")))))))).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new RootElement(new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new DivisorElement(new Formula().add(new SimpleElement("beta")), new Formula().add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("2"))))))))));
        formula.add(new OperatorElement("+"));
//        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("2"))));
//        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")), new Formula().add(new SimpleElement("e", new Formula().add(new SimpleElement("x"))))));

        final CanvasPanelExt canvas1 = new CanvasPanelExt(430, 150);


        final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
        final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();


        final FormulaDrawer drawer = new FormulaDrawer() {

            CursorPosition cursor = new CursorPosition(new SimpleElement(""), 0, 0, 0, 0, 0);
            FormulaItem highlightedItem = new SimpleElement("");

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

            public Metrics textMetrics(String text, int size) {
                canvas1.setFontSize(size);
                TextMetrics metrics = canvas1.measureText(text);
                return new Metrics(metrics.getWidth(), metrics.getHeight() / 2, metrics.getHeight() / 2);
            }

            public void drawText(String text, int size, int x, int y) {
                canvas1.setFontSize(size);
                TextMetrics metrics = canvas1.measureText(text);
                canvas1.strokeText(text, (int) (x + metrics.getWidth()), (int) (y - metrics.getHeight() / 2));
            }

            public int getSmallerSize(int size) {
                return size * 3 / 4;
            }

            public CanvasPanelExt getCanvas() {
                return canvas1;
            }

            private boolean setCursor(CursorPosition newCursor) {
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

            public void redraw() {
                canvas1.clear();

                canvas1.setAlign(net.kornr.abstractcanvas.client.gwt.CanvasPanelExt.ALIGN_END);
                canvas1.setFillStyle(Color.WHITE);
                canvas1.setGlobalAlpha(1.0);
                canvas1.fillRect(0, 0, canvas1.getCoordWidth(), canvas1.getCoordHeight());

                canvas1.setStrokeStyle(Color.BLACK);
                canvas1.setLineWidth(1);

                Metrics metrics = formula.drawAligned(this, 10, 10, 20, FormulaDrawer.Align.TOP);
                canvas1.beginPath();
                canvas1.rect(9, 9, 2 + metrics.getWidth(), 2 + metrics.getHeight());
                canvas1.stroke();

                canvas1.beginPath();
                canvas1.moveTo(cursor.getX(), cursor.getY() - cursor.getHeightUp());
                canvas1.lineTo(cursor.getX(), cursor.getY() + cursor.getHeightDown());
                canvas1.stroke();
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
                    canvas1.setFillStyle(Color.DARK_BLUE);
                    canvas1.fillRect(minRect.getX(), minRect.getY(), minRect.getWidth(), minRect.getHeight());
                }

                return minRectItem;
            }
        };

        canvas1.setCoordSize(430, 150);

        canvas1.addCanvasPainter(new CanvasPainter() {

            public void drawCanvas(ICanvasExt canvas) {
                drawer.redraw();
                /*                canvas.clear();

                canvas.setAlign(net.kornr.abstractcanvas.client.gwt.CanvasPanelExt.ALIGN_END);
                canvas.setFillStyle(Color.WHITE);
                canvas.setGlobalAlpha(1.0);
                canvas.fillRect(0, 0, canvas1.getCoordWidth(), canvas1.getCoordHeight());

                canvas.setStrokeStyle(Color.BLACK);
                canvas.setLineWidth(1);

                Metrics metrics = formula.drawAligned(drawer, 10, 10, 20, FormulaDrawer.Align.TOP);
                canvas.beginPath();
                canvas.rect(9, 9, 2 + metrics.getWidth(), 2 + metrics.getHeight());
                canvas.stroke();*/

//                formula.drawAligned(drawer, 10, 100, 20, FormulaDrawer.Align.BOTTOM);
//                formula.drawAligned(drawer, 10, 50, 20, FormulaDrawer.Align.MIDDLE);

            }
        });

        RootPanel.get().add(canvas1, 10, 10);

        FocusPanel panel = new FocusPanel();
        panel.setSize("430", "150");

        panel.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                if (event.isLeftArrow()) {
                    drawer.moveCursorLeft();
                } else if (event.isRightArrow() || event.isControlKeyDown()) {
                    drawer.moveCursorRight();
                } else if (event.isUpArrow()) {
                    drawer.moveCursorUp();
                } else if (event.isDownArrow()) {
                    drawer.moveCursorDown();
                }
//                canvas1.strokeText("code:"+event.isLeftArrow(), 300, 10);
//                canvas1.strokeText("code:"+event.getNativeKeyCode(), 300, 30);
            }
        });

        panel.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                drawer.insert(event.getCharCode());
//                canvas1.strokeText("code:" + event.getCharCode(), 200, 10);
//                canvas1.strokeText("code:" + event.getAssociatedType().getName(), 200, 40);
            }
        });

        panel.addMouseMoveHandler(new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
//                drawer.highlightItemAt(event.getX(), event.getY());
            }
        });

        panel.addMouseUpHandler(new MouseUpHandler() {

            public void onMouseUp(MouseUpEvent event) {
                drawer.selectItemAt(event.getX(), event.getY());
            }
        });

        RootPanel.get().add(panel, 10, 10);




    }
}
