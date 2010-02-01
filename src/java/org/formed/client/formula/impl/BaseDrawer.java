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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    protected Cursor cursorFrom = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor cursorHighlight = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor selectionCursorFrom = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor selectionCursorTo = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected FormulaItem highlighted1 = null;
    protected FormulaItem highlighted2 = null;
    protected final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
    protected final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();
    protected final Drawer THIS_DRAWER = this;
    protected Metrics drawerMetrics = new Metrics(0, 0, 0);
    protected int debugTexts = 0;
    protected SimpleCursorFixer fixer = new SimpleCursorFixer();

    public BaseDrawer(Formula formula) {
        this.formula = formula;
        this.undoer = Undoer.ZERO_UNDOER;

        populateFixer();

        formula.invalidateMetrics();
    }

    public BaseDrawer(Formula formula, Undoer undoer) {
        this.formula = formula;
        this.undoer = undoer;

        populateFixer();

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
        markSelection(cursorFrom, cursor);
    }

    protected void postRedraw() {
        if (cursor != null) {
            cursor.reMeasure(this);
        }
        if (cursorHighlight != null) {
            cursorHighlight.reMeasure(this);
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

    protected void populateFixer() {
        fixer.clear();
        fixer.addCursor(cursor);
        fixer.addCursor(cursorFrom);
        fixer.addCursor(cursorHighlight);
    }

    private boolean setCursor(Cursor newCursor) {
        if (newCursor == null) {
            return false;
        }

        cursor = newCursor;
        populateFixer();
        redraw();
        return true;
    }

    public boolean moveCursorUp() {
        //return setCursor(cursor.getItem().getUp(cursor.getPosition()));
        cursor.moveUp();
        redraw();
        return true;
    }

    public boolean moveCursorDown() {
        //return setCursor(cursor.getItem().getDown(cursor.getPosition()));
        cursor.moveDown();
        redraw();
        return true;
    }

    public boolean moveCursorLeft() {
        //return setCursor(cursor.getItem().getLeft(cursor.getPosition()));
        cursor.moveLeft();
        redraw();
        return true;
    }

    public boolean moveCursorRight() {
        //return setCursor(cursor.getItem().getRight(cursor.getPosition()));
        cursor.moveRight();
        redraw();
        return true;
    }

    public void insertElement(FormulaItem newItem) {
        if (newItem == null || cursor == null) {
            return;
        }

        FormulaItem currentItem = cursor.getItem();
        if (currentItem == null) {
            return;
        }

        Command command = null;
        switch (currentItem.getHowToInsert(cursor, newItem)) {
            case INSERT:
                command = currentItem.buildInsert(cursor, newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            case BREAK:
                command = currentItem.buildBreakWith(cursor, newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateLeft(fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateRight(fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            case LEFT:
                command = currentItem.buildInsertBefore(newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateLeft(fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateRight(fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            case RIGHT:
                command = currentItem.buildInsertAfter(newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateLeft(fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateRight(fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            default:
        }
        redraw();
    }

    protected void lightInsertElement(FormulaItem newItem) {
        if (newItem == null || cursor == null) {
            return;
        }

        FormulaItem currentItem = cursor.getItem();
        if (currentItem == null) {
            return;
        }

        Command command = null;
        switch (currentItem.getHowToInsert(cursor, newItem)) {
            case INSERT:
                command = currentItem.buildInsert(cursor, newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            case BREAK:
                command = currentItem.buildBreakWith(cursor, newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);

            case LEFT:
                command = currentItem.buildInsertBefore(newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);

            case RIGHT:
                command = currentItem.buildInsertAfter(newItem, fixer);
                setCursor(command.execute());
                undoer.add(command);

            default:
        }
    }

    public void insert(char c) {
        if (selected) {
            deleteSelection();
        }

        if (c == '^') {
            if (cursor.getItem() instanceof PoweredElement) {
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
            insertElement(new DivisorElement());
        } else {
            insertElement(new SimpleElement("" + c));
        }
    }

    public void deleteLeft() {
        if (deleteSelection()) {
            return;
        }

        Command command = cursor.getItem().buildDeleteLeft(cursor, fixer);
        setCursor(command.execute());
        undoer.add(command);

        redraw();
    }

    public void deleteRight() {
        if (deleteSelection()) {
            return;
        }

        Command command = cursor.getItem().buildDeleteRight(cursor, fixer);
        setCursor(command.execute());
        undoer.add(command);

        redraw();
    }

    protected boolean deleteSelection() {
        if (selected) {
            selected = false;
            for (int pos = selectedPosTo; pos >= selectedPosFrom; pos--) {
                FormulaItem item = selectedParent.getItem(pos);
                selectedParent.remove(item);
                //fixer.removed(item, );
            }
            FormulaItem item = selectedParent.getItem(selectedPosFrom);
            if (item != null) {
                setCursor(item.getFirst());
            } else {
                setCursor(selectedParent.getFirst());
            }
            return true;
        }
        return false;
    }
    protected final List<FormulaItem> copiedItems = new ArrayList<FormulaItem>();

    public void cut() {
        copy();
        deleteSelection();
        redraw();
    }

    public void copy() {
        copiedItems.clear();
        for (int pos = selectedPosFrom; pos <= selectedPosTo; pos++) {
            copiedItems.add(selectedParent.getItem(pos).makeClone());
        }
    }

    public void paste() {
        if (selected) {
            deleteSelection();
        }

        for (FormulaItem item : copiedItems) {
            FormulaItem newItem = item.makeClone();
            lightInsertElement(newItem);
            setCursor(newItem.getLast());
        }
        redraw();
//        drawText("" + copiedItems.size(), 20, 10, 40);
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
    protected boolean selecting = false;
    protected boolean canMakeSelection = false;
    protected boolean selected = false;
    protected Formula selectedParent = null;
    protected int selectedPosFrom = 0;
    protected int selectedPosTo = 0;

    protected boolean markSelection(Cursor from, Cursor to) {
        if (selected) {
            for (int pos = selectedPosFrom; pos <= selectedPosTo; pos++) {
                selectedParent.getItem(pos).highlightOff();
            }
        }
        selected = false;

        if (!canMakeSelection) {
            return false;
        }

        FormulaItem itemFrom = from.getItem();
        FormulaItem itemTo = to.getItem();
        int posFrom = from.getPosition();
        int posTo = to.getPosition();

        if (itemFrom == itemTo && posFrom == posTo) {
            return false;
        }

        Formula parentFrom = itemFrom.getParent();
        Formula parentTo = itemTo.getParent();
        if (parentFrom == null || parentTo == null) {
            return false;
        }

        Formula parent = parentFrom;
        int itemPosFrom = parentFrom.getItemPosition(itemFrom);
        int itemPosTo = parentTo.getItemPosition(itemTo);
        if (parentFrom == parentTo) { //All in one formula
        } else {
            int pos = parentFrom.posInsideYou(itemTo);
            if (pos >= 0) {//itemTo is inside itemFroms parent
                itemPosTo = pos;
            } else {
                pos = parentTo.posInsideYou(itemFrom);
                if (pos >= 0) {//itemFrom is inside itemTos parent
                    itemPosFrom = pos;
                    parent = parentTo;
                } else {
                    return false;
                }
            }
        }

        if (itemPosTo < itemPosFrom) {
            int pos = itemPosFrom;
            itemPosFrom = itemPosTo;
            itemPosTo = pos;
        }

        selected = true;
        selectedParent = parent;
        selectedPosFrom = itemPosFrom;
        selectedPosTo = itemPosTo;

        for (int pos = itemPosFrom; pos <= itemPosTo; pos++) {
            parent.getItem(pos).setHighlight(255, 255, 0);
        }

        return true;
    }

    public void mouseDownAt(int x, int y) {
        FormulaItem minRectItem = findItemAt(x, y);

        if (minRectItem != null) {
            selecting = true;
            canMakeSelection = true;
            cursorFrom = minRectItem.getCursor(this, x, y);
            cursor = cursorFrom;
        }

        populateFixer();
        redraw();
    }

    public void mouseUpAt(int x, int y) {
        FormulaItem minRectItem = findItemAt(x, y);

        if (minRectItem != null) {
            cursor = minRectItem.getCursor(this, x, y);
            canMakeSelection = markSelection(cursorFrom, cursor);
        } else {
            canMakeSelection = false;
        }

        selecting = false;

        populateFixer();
        redraw();
    }

    public void mouseMoveAt(int x, int y) {
        if (selecting) {
            FormulaItem minRectItem = findItemAt(x, y);

            if (minRectItem != null) {
                selecting = true;
                cursor = minRectItem.getCursor(this, x, y);
            }
        } else {
            highlightItemAt(x, y);
        }

        populateFixer();
        redraw();
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
            cursorHighlight = minRectItem.getCursor(this, x, y);

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
            cursorHighlight = null;
        }
        redraw();

        return minRectItem;
    }
}
