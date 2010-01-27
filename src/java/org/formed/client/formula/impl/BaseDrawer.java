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

import java.util.HashMap;
import java.util.Map;
import org.formed.client.formula.Command;
import org.formed.client.formula.Cursor;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;
import org.formed.client.formula.Rectangle;
import org.formed.client.formula.Undoer;
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

    protected Formula formula;
    protected final Undoer undoer;
    protected Cursor cursor = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor cursor2 = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    private FormulaItem highlightedItem = new SimpleElement("");
    protected final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
    protected final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();
    protected final Drawer THIS_DRAWER = this;
    protected Metrics drawerMetrics = new Metrics(0, 0, 0);

    protected int debugTexts = 0;

    public BaseDrawer(Formula formula) {
        this.formula = formula;
        this.undoer = Undoer.ZERO_UNDOER;

        formula.invalidateMetrics();
    }

    public BaseDrawer(Formula formula, Undoer undoer) {
        this.formula = formula;
        this.undoer = undoer;

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

    public void drawDebugText(String text) {
//        drawText(text, 10, 5, 30+debugTexts*15);
//        debugTexts++;
    }

    protected void preRedraw() {
        items.clear();
        formulas.clear();
        debugTexts = 0;
    }

    protected void postRedraw() {
        if (cursor != null) {
            cursor.reMeasure(this);
        }
        if (cursor2 != null) {
            cursor2.reMeasure(this);
        }
        redrawCursor();
    }

    public abstract void redraw();

    public abstract void redrawCursor();

    public Metrics getDrawerMetrics() {
        return drawerMetrics.cloneMetrics();
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
        return setCursor(cursor.getItem().getUp(cursor.getPosition()));
    }

    public boolean moveCursorDown() {
        return setCursor(cursor.getItem().getDown(cursor.getPosition()));
    }

    public boolean moveCursorLeft() {
        return setCursor(cursor.getItem().getLeft(cursor.getPosition()));
    }

    public boolean moveCursorRight() {
        return setCursor(cursor.getItem().getRight(cursor.getPosition()));
    }

    protected Command makeCommandBreakWith(final SimpleElement currentItem, final FormulaItem newItem, final int pos) {
        final Formula PARENT_BACKUP = currentItem.getParent();
        return new Command() {

            public Cursor execute() {
                currentItem.breakWith(pos, newItem);
                return newItem.getLast();
            }

            public void undo() {
                PARENT_BACKUP.remove(newItem);
            }
        };
    }

    protected Command makeCommandInsertAfter(final FormulaItem currentItem, final FormulaItem newItem) {
        final Formula PARENT_BACKUP = currentItem.getParent();
        return new Command() {

            public Cursor execute() {
                PARENT_BACKUP.insertAfter(newItem, currentItem);
                return newItem.getLast();
            }

            public void undo() {
                PARENT_BACKUP.remove(newItem);
            }
        };
    }

    protected Command makeCommandInsertBefore(final FormulaItem currentItem, final FormulaItem newItem) {
        final Formula PARENT_BACKUP = currentItem.getParent();
        return new Command() {

            public Cursor execute() {
                PARENT_BACKUP.insertBefore(newItem, currentItem);
                return newItem.getLast();
            }

            public void undo() {
                PARENT_BACKUP.remove(newItem);
            }
        };
    }

    protected Command makeCommandInsertCharInSimple(final FormulaItem item, final int pos, final char c) {
        return new Command() {

            public Cursor execute() {
                Cursor newCursor = item.insertChar(pos, c);
                return newCursor;
            }

            public void undo() {
                item.removeChar(pos);
            }
        };
    }

    protected Command makeCommandInsertChar(final FormulaItem item, final int pos, final FormulaItem newItem) {
        return new Command() {

            public Cursor execute() {
                Cursor newCursor = item.insertChar(pos, newItem);
                return newCursor;
            }

            public void undo() {
                item.removeChar(pos);
            }
        };
    }

    public void insert(char c) {
        final FormulaItem currentItem = cursor.getItem();
        SimpleElement currentSimpleItem = null;
        boolean currentSimple = false;
        if (currentItem instanceof SimpleElement) {
            currentSimpleItem = (SimpleElement) currentItem;
            currentSimple = true;
        }

        Cursor rightCursor = currentItem.getParent().getYourRight(currentItem);
        SimpleElement rightSimpleItem = null;
        boolean rightSimple = false;
        if (rightCursor != null) {
            rightCursor.setPosition(0);
            if (rightCursor.getItem() instanceof SimpleElement) {
                rightSimpleItem = (SimpleElement) rightCursor.getItem();
                rightSimple = true;
            }
        }

        if (c == '^') {
            if (currentItem instanceof PoweredElement) {
                moveCursorUp();
            }
        } else if (c == '+') {
            insertElement(new OperatorElement("+"));
        } else if (c == '-') {
            insertElement(new OperatorElement("-"));
        } else if (c == '*') {
            insertElement(new OperatorElement("·")); //*·×
        } else if (c == '<') {
            insertElement(new OperatorElement("<"));
        } else if (c == '>') {
            insertElement(new OperatorElement(">"));
        } else if (c == '=') {
            insertElement(new OperatorElement("="));
        } else if (c == '(') {
            insertElement(new LeftCloser());
        } else if (c == ')') {
            insertElement(new RightCloser());
        } else if (c == '/') {
            if (currentSimple) {
                if (cursor.getPosition() < currentSimpleItem.getName().length()) {
                    Formula formula1 = new Formula(true).add(new SimpleElement(currentSimpleItem.getTextBefore(cursor)));
                    Formula formula2 = new Formula(true).add(new SimpleElement(currentSimpleItem.getTextAfter(cursor), currentSimpleItem.getPower()));
                    DivisorElement newDivisor = new DivisorElement(formula1, formula2);

                    currentItem.getParent().replace(newDivisor, currentItem);
                    setCursor(newDivisor.getFormula2().getFirst());
                } else {
                    Formula formula1 = new Formula(true).add(new SimpleElement(currentSimpleItem.getTextBefore(cursor), currentSimpleItem.getPower()));
                    Formula formula2 = new Formula(true);
                    DivisorElement newDivisor = new DivisorElement(formula1, formula2);

                    currentItem.getParent().replace(newDivisor, currentItem);
                    setCursor(newDivisor.getFormula2().getFirst());
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

                DivisorElement newDivisor = new DivisorElement(formula1, formula2);
                currentFormula.insertAt(position, newDivisor);
                setCursor(newDivisor.getFormula2().getFirst());
            } else {
                DivisorElement newDivisor = new DivisorElement();
                if (cursor.getPosition() == 0) {
                    currentItem.getParent().insertBefore(newDivisor, currentItem);
                    undoer.add(makeCommandInsertBefore(currentItem, newDivisor));
                } else {
                    currentItem.getParent().insertAfter(newDivisor, currentItem);
                    undoer.add(makeCommandInsertAfter(currentItem, newDivisor));
                }
                setCursor(newDivisor.getFormula1().getFirst());
            }
        } else {
            Command command;
            if (currentSimple) {
                command = makeCommandInsertCharInSimple(currentItem, cursor.getPosition(), c);
            } else if (rightSimple) {
                command = makeCommandInsertCharInSimple(rightSimpleItem, rightCursor.getPosition(), c);
            } else {
                SimpleElement newSimple = new SimpleElement("" + c);
                command = makeCommandInsertChar(currentItem, cursor.getPosition(), newSimple);
            }
            setCursor(command.execute());
            undoer.add(command);
        }

        redraw();
    }

    public void insertElement(final FormulaItem newItem) {
        final FormulaItem currentItem = cursor.getItem();
        if (currentItem == null) {
            return;
        }

        Command command;

        if (currentItem instanceof SimpleElement) {
            command = makeCommandBreakWith((SimpleElement) currentItem, newItem, cursor.getPosition());
        } else {
            if (cursor.getPosition() == 0) {
                command = makeCommandInsertBefore(currentItem, newItem);
            } else {
                command = makeCommandInsertAfter(currentItem, newItem);
            }
        }

        setCursor(command.execute());
        undoer.add(command);
        redraw();
    }

    public void deleteLeft() {
        Command command = cursor.getItem().deleteLeft(cursor);
        setCursor(command.execute());
        undoer.add(command);

        redraw();
    }

    public void deleteRight() {
        Command command = cursor.getItem().deleteRight(cursor);
        setCursor(command.execute());
        undoer.add(command);

        redraw();
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

        if (minRectItem != null) {
            cursor = minRectItem.getCursor(this, x, y);
            redraw();
        }

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
        if (minRectItem != null) {
            cursor2 = minRectItem.getCursor(this, x, y);
        } else {
            cursor2 = null;
        }
        redraw();

        return minRectItem;
        /*        Rectangle minRect = null;
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

        return minRectItem;*/
    }
}
