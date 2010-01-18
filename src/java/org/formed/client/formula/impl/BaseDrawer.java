/*
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

import java.util.HashMap;
import java.util.Map;
import org.formed.client.formula.Cursor;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;
import org.formed.client.formula.Rectangle;
import org.formed.client.formula.elements.DivisorElement;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.PoweredElement;
import org.formed.client.formula.elements.RightCloser;
import org.formed.client.formula.elements.SimpleElement;

/**
 *
 * @author Bulat Sirazetdinov
 */
public abstract class BaseDrawer implements Drawer {

    protected final Formula formula;
    protected Cursor cursor = new Cursor(this, new SimpleElement(""), 0, 0, 0, 0, 0);
    private FormulaItem highlightedItem = new SimpleElement("");
    private final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
    private final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();

    public BaseDrawer(Formula formula) {
        this.formula = formula;

        formula.invalidateMetrics();
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

    public abstract void redraw();

    public abstract void redrawCursor();

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
    }

    public boolean moveCursorDown() {
        return setCursor(cursor.getItem().getDown(this, cursor.getPosition()));
    }

    public boolean moveCursorLeft() {
        return setCursor(cursor.getItem().getLeft(this, cursor.getPosition()));
    }

    public boolean moveCursorRight() {
        return setCursor(cursor.getItem().getRight(this, cursor.getPosition()));
    }

    public void insert(char c) {
        FormulaItem currentItem = cursor.getItem();
        SimpleElement currentSimpleItem = null;
        boolean currentSimple = false;
        if (currentItem instanceof SimpleElement) {
            currentSimpleItem = (SimpleElement) currentItem;
            currentSimple = true;
        }

        Cursor rightCursor = currentItem.getParent().getYourRight(this, currentItem);
        SimpleElement rightSimpleItem = null;
        boolean rightSimple = false;
        if (rightCursor != null) {
            rightCursor.setPosition(0);
            if (rightCursor.getItem() instanceof SimpleElement) {
                rightSimpleItem = (SimpleElement) rightCursor.getItem();
                rightSimple = true;
            }
        }

        switch (c) {
            case '+':
            case '-':
            case '*': {
                OperatorElement newItem = new OperatorElement("" + c);
                if (currentSimple) {
                    currentSimpleItem.breakWith(this, cursor, newItem);
                    setCursor(newItem.getLast(this));
                } else {
                    if (cursor.getPosition() == 0) {
                        currentItem.getParent().insertBefore(newItem, currentItem);
                    } else {
                        currentItem.getParent().insertAfter(newItem, currentItem);
                    }
                    setCursor(newItem.getLast(this));
                }
            }
            break;

            case '(': {
                LeftCloser newItem = new LeftCloser();
                if (currentSimple) {
                    setCursor(currentSimpleItem.breakWith(this, cursor, newItem));
                    setCursor(newItem.getLast(this));
                } else {
                    if (cursor.getPosition() == 0) {
                        currentItem.getParent().insertBefore(newItem, currentItem);
                    } else {
                        currentItem.getParent().insertAfter(newItem, currentItem);
                    }
                    setCursor(newItem.getLast(this));
                }
            }
            break;
            case ')': {
                RightCloser newItem = new RightCloser();
                if (currentSimple) {
                    setCursor(currentSimpleItem.breakWith(this, cursor, newItem));
                    setCursor(newItem.getLast(this));
                } else {
                    if (cursor.getPosition() == 0) {
                        currentItem.getParent().insertBefore(newItem, currentItem);
                    } else {
                        currentItem.getParent().insertAfter(newItem, currentItem);
                    }
                    setCursor(newItem.getLast(this));
                }
            }
            break;

            case '/': {
                if (currentSimple) {
                    if (cursor.getPosition() < currentSimpleItem.getName().length()) {
                        Formula formula1 = new Formula(true).add(new SimpleElement(currentSimpleItem.getTextBefore(cursor)));
                        Formula formula2 = new Formula(true).add(new SimpleElement(currentSimpleItem.getTextAfter(cursor), currentSimpleItem.getPower()));
                        DivisorElement newItem = new DivisorElement(formula1, formula2);

                        currentItem.getParent().replace(newItem, currentItem);
                        setCursor(newItem.getFormula2().getFirst(this));
                    } else {
                        Formula formula1 = new Formula(true).add(new SimpleElement(currentSimpleItem.getTextBefore(cursor), currentSimpleItem.getPower()));
                        Formula formula2 = new Formula(true);
                        DivisorElement newItem = new DivisorElement(formula1, formula2);

                        currentItem.getParent().replace(newItem, currentItem);
                        setCursor(newItem.getFormula2().getFirst(this));
                    }
                } else if (currentItem instanceof RightCloser) {
                    Formula formula1 = new Formula(true);
                    Formula formula2 = new Formula(true);

                    Formula currentFormula = currentItem.getParent();
                    int rights = 1;
                    int position = currentFormula.getItemPosition(currentItem);
                    formula1.add(currentItem.makeClone());
                    currentFormula.remove(currentItem);
                    while (rights > 0) {
                        position--;
                        FormulaItem item = currentFormula.getItem(position);
                        if (item instanceof RightCloser) {
                            rights++;
                        } else if (item instanceof LeftCloser) {
                            rights--;
                        }
                        formula1.insertAt(0, item.makeClone());
                        currentFormula.remove(item);
                        if (position <= 0) {
                            break;
                        }
                    }

                    DivisorElement newItem = new DivisorElement(formula1, formula2);
                    currentFormula.insertAt(position, newItem);
                    setCursor(newItem.getFormula2().getFirst(this));
                } else {
                    DivisorElement newItem = new DivisorElement();
                    if (cursor.getPosition() == 0) {
                        currentItem.getParent().insertBefore(newItem, currentItem);
                    } else {
                        currentItem.getParent().insertAfter(newItem, currentItem);
                    }
                    setCursor(newItem.getFormula1().getFirst(this));
                }
            }
            break;

            case '^':
                if (currentItem instanceof PoweredElement) {
                    moveCursorUp();
                }
                break;

            default:
                if (currentSimple) {
                    setCursor(currentItem.insertChar(this, cursor, c));
                } else if (rightSimple) {
                    setCursor(rightSimpleItem.insertChar(this, rightCursor, c));
                } else {
                    setCursor(currentItem.insertChar(this, cursor, c));
                }
        }
        redraw();
        cursor.reMeasure(this);
        redraw();
    }

    public void deleteLeft() {
    }

    public void deleteRight() {
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
        }

        return minRectItem;
    }
}
