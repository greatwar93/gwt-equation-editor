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

    protected final Formula formula;
    protected final Undoer undoer;
    protected Cursor cursor = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor cursor2 = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected FormulaItem highlighted1 = null;
    protected FormulaItem highlighted2 = null;
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

    public abstract Metrics textMetrics(String text, int size);

    public Metrics getDrawerMetrics() {
        return drawerMetrics.cloneMetrics();
    }

    public int sizeForHeight(String text, int height) {
        int sizeFrom = 1;
        int sizeTo = 4000;

        while (sizeFrom != sizeTo) {
            int size = (sizeTo + sizeFrom) / 2;
            Metrics metrics = textMetrics(text, size);
            if (height > metrics.getHeight()) {
                if (size == sizeFrom) {
                    return size;
                }
                sizeFrom = size;
            } else if (height < metrics.getHeight()) {
                if (size == sizeTo) {
                    return size;
                }
                sizeTo = size;
            } else {
                return size;
            }
        }

        return sizeFrom;
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

    protected Command makeBreakWith(final SimpleElement currentItem, final FormulaItem newItem, final int pos) {
        //return currentItem.makeBreakWith(pos, newItem);
        final Formula parent_backup = currentItem.getParent();
        return new Command() {

            public Cursor execute() {
                currentItem.breakWith(pos, newItem);
                return newItem.getLast();
            }

            public void undo() {
                parent_backup.remove(newItem);
                if (!currentItem.isAddNext()) {
                    return;
                }

                currentItem.setAddNext(false);
                FormulaItem item = parent_backup.getRightItem(currentItem);
                if (!(item instanceof SimpleElement)) {
                    return;
                }

                currentItem.addItem((SimpleElement) item);
                parent_backup.remove(item);
            }
        };
    }

    protected Command makeInsertAfter(final FormulaItem currentItem, final FormulaItem newItem) {
        final Formula parent_backup = currentItem.getParent();
        return new Command() {

            public Cursor execute() {
                parent_backup.insertAfter(newItem, currentItem);
                return newItem.getLast();
            }

            public void undo() {
                parent_backup.remove(newItem);
            }
        };
    }

    protected Command makeInsertBefore(final FormulaItem currentItem, final FormulaItem newItem) {
        final Formula parent_backup = currentItem.getParent();
        return new Command() {

            public Cursor execute() {
                parent_backup.insertBefore(newItem, currentItem);
                return newItem.getLast();
            }

            public void undo() {
                parent_backup.remove(newItem);
            }
        };
    }

    protected Command makeInsertCharInSimple(final FormulaItem item, final int pos, final char c) {
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

    protected Command makeInsertChar(final FormulaItem item, final int pos, final FormulaItem newItem) {
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

    /*
     * Move specified formula part to another formula
     */
    protected void moveFormula(Formula source, int posFrom, int size, Formula dest, int posTo) {
        for (int i = 0; i < size; i++) {
            FormulaItem item = source.getItem(posFrom);
            if (item == null) {
                return;
            }
            source.remove(item);
            dest.insertAt(posTo, item);
            posTo++;
        }
    }

    protected Command makeLeftToDivisor(final DivisorElement newItem) {
        final Formula parent_backup = newItem.getParent();
        if (parent_backup == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem item = parent_backup.getLeftItem(newItem);
        if (item == null) {
            return Command.ZERO_COMMAND;
        }

        if (item instanceof SimpleElement) {
            return new Command() {

                FormulaItem item2 = null;

                public Cursor execute() {
                    item2 = parent_backup.getLeftItem(newItem);
                    if (item2 != null) {
                        parent_backup.remove(item2);
                        newItem.getFormula1().add(item2);
                    }
                    return newItem.getFormula2().getFirst();
                }

                public void undo() {
                    if (item2 != null) {
                        newItem.getFormula1().remove(item2);
                        parent_backup.insertBefore(item2, newItem);
                        item2 = null;
                    }
                }
            };
        } else if (item instanceof RightCloser) {
            int posTo = parent_backup.getItemPosition(item);
            final int posFrom = parent_backup.findLeftCloserPos(posTo);
            final int size = posTo - posFrom + 1;
            return new Command() {

                public Cursor execute() {
                    moveFormula(parent_backup, posFrom, size, newItem.getFormula1(), 0);
                    return newItem.getFormula1().getLast();
                }

                public void undo() {
                    moveFormula(newItem.getFormula1(), 0, size, parent_backup, posFrom);
                }
            };
        }

        return Command.ZERO_COMMAND;
    }

    protected Command makeRightToDivisor(final DivisorElement newItem) {
        final Formula parent_backup = newItem.getParent();
        if (parent_backup == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem item = parent_backup.getRightItem(newItem);
        if (item == null) {
            return Command.ZERO_COMMAND;
        }

        if (item instanceof SimpleElement) {
            return new Command() {

                FormulaItem item2 = null;

                public Cursor execute() {
                    item2 = parent_backup.getRightItem(newItem);
                    if (item2 != null) {
                        parent_backup.remove(item2);
                        newItem.getFormula2().add(item2);
                    }
                    return newItem.getLast();
                }

                public void undo() {
                    if (item2 != null) {
                        newItem.getFormula2().remove(item2);
                        parent_backup.insertAfter(item2, newItem);
                        item2 = null;
                    }
                }
            };
        } else if (item instanceof LeftCloser) {
            final int posFrom = parent_backup.getItemPosition(item);
            int posTo = parent_backup.findRightCloserPos(posFrom);
            final int size = posTo - posFrom + 1;
            return new Command() {

                public Cursor execute() {
                    moveFormula(parent_backup, posFrom, size, newItem.getFormula2(), 0);
                    return newItem.getLast();
                }

                public void undo() {
                    moveFormula(newItem.getFormula2(), 0, size, parent_backup, posFrom);
                }
            };
        }

        return Command.ZERO_COMMAND;
    }

    public void insert(char c) {
        final FormulaItem currentItem = cursor.getItem();

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
            DivisorElement newItem = new DivisorElement();
            insertElement(newItem);

            Command leftCommand = makeLeftToDivisor(newItem);
            setCursor(leftCommand.execute());
            undoer.add(leftCommand);

            Command rightCommand = makeRightToDivisor(newItem);
            setCursor(rightCommand.execute());
            undoer.add(rightCommand);
        } else {
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

            Command command;
            if (currentItem instanceof SimpleElement) {
                command = makeInsertCharInSimple(currentItem, cursor.getPosition(), c);
            } else if (rightSimple) {
                command = makeInsertCharInSimple(rightSimpleItem, rightCursor.getPosition(), c);
            } else {
                SimpleElement newSimple = new SimpleElement("" + c);
                command = makeInsertChar(currentItem, cursor.getPosition(), newSimple);
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
            command = makeBreakWith((SimpleElement) currentItem, newItem, cursor.getPosition());
        } else {
            if (cursor.getPosition() == 0) {
                command = makeInsertBefore(currentItem, newItem);
            } else {
                command = makeInsertAfter(currentItem, newItem);
            }
        }

        setCursor(command.execute());
        undoer.add(command);
        //redraw();
    }

    public void deleteLeft() {
        Command command = cursor.getItem().makeDeleteLeft(cursor);
        setCursor(command.execute());
        undoer.add(command);

        redraw();
    }

    public void deleteRight() {
        Command command = cursor.getItem().makeDeleteRight(cursor);
        setCursor(command.execute());
        undoer.add(command);

        redraw();
    }

    public FormulaItem findItemAt(int x, int y) {
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

        return minRectItem;
    }

    public FormulaItem selectItemAt(int x, int y) {
        FormulaItem minRectItem = findItemAt(x, y);

        if (minRectItem != null) {
            cursor = minRectItem.getCursor(this, x, y);
            redraw();
        }

        return minRectItem;
    }

    public FormulaItem highlightItemAt(int x, int y) {
        //Switch off highlighting
        if (highlighted1 != null) {
            highlighted1.highlightOff();
            highlighted1 = null;
        }
        if (highlighted2 != null) {
            highlighted2.highlightOff();
            highlighted2 = null;
        }

        FormulaItem minRectItem = findItemAt(x, y);

        if (minRectItem != null) {
            cursor2 = minRectItem.getCursor(this, x, y);

            //Highlight Closer items
            if (minRectItem instanceof LeftCloser) {
                highlighted1 = minRectItem;
                highlighted2 = ((LeftCloser) minRectItem).getRightCloser();
                if (highlighted2 != null) {
                    highlighted1.setHighlight(255, 255, 0);
                    highlighted2.setHighlight(255, 255, 0);
                } else {
                    highlighted1.setHighlight(255, 0, 0);
                }
            } else if (minRectItem instanceof RightCloser) {
                highlighted1 = minRectItem;
                highlighted2 = ((RightCloser) minRectItem).getLeftCloser();
                if (highlighted2 != null) {
                    highlighted1.setHighlight(255, 255, 0);
                    highlighted2.setHighlight(255, 255, 0);
                } else {
                    highlighted1.setHighlight(255, 0, 0);
                }
            }
        } else {
            cursor2 = null;
        }
        redraw();

        return minRectItem;
    }
}
