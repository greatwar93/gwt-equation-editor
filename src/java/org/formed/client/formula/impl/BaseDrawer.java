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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.formed.client.formula.Command;
import org.formed.client.formula.Cursor;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;
import org.formed.client.formula.Rectangle;
import org.formed.client.formula.Undoer;
import org.formed.client.formula.elements.DivisorElement;
import org.formed.client.formula.elements.FunctionElement;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.PoweredElement;
import org.formed.client.formula.elements.RightCloser;
import org.formed.client.formula.elements.RootElement;
import org.formed.client.formula.elements.SimpleElement;

/**
 *
 * @author Bulat Sirazetdinov
 */
public abstract class BaseDrawer implements Drawer {

    protected final Formula formula;
    protected final Undoer undoer;
    protected final Drawer THIS_DRAWER = this;
    protected Metrics drawerMetrics = new Metrics(0, 0, 0);
    protected int debugTexts = 0;
    protected final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
    protected final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();
    protected SimpleCursorFixer fixer = new SimpleCursorFixer();
    //Cursors
    protected Cursor cursor = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor cursorFrom = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected Cursor cursorHighlight = new Cursor(new SimpleElement(""), 0, 0, 0, 0, 0);
    protected FormulaItem highlighted1 = null;
    protected FormulaItem highlighted2 = null;
    //Selection handling
    protected boolean selecting = false;
    protected boolean canMakeSelection = false;
    protected boolean selected = false;
    protected Formula selectedParent = null;
    protected int selectedPosFrom = 0;
    protected int selectedPosTo = 0;

    public BaseDrawer(Formula formula) {
        this.formula = formula;
        this.undoer = Undoer.ZERO_UNDOER;

        populateFixer();

        formula.invalidateMetrics();
        populateAuto();
    }

    public BaseDrawer(Formula formula, Undoer undoer) {
        this.formula = formula;
        this.undoer = undoer;

        populateFixer();

        formula.invalidateMetrics();
        populateAuto();
    }

    private void populateAuto() {
        autoSimple.add(new AutoCompletion("α", "alfa", "α", new SimpleElement("α"), false));
        autoSimple.add(new AutoCompletion("α", "alpha", "α", new SimpleElement("α"), false));
        autoSimple.add(new AutoCompletion("α", "альфа", "α", new SimpleElement("α"), false));

        autoSimple.add(new AutoCompletion("β", "beta", "β", new SimpleElement("β"), false));
        autoSimple.add(new AutoCompletion("β", "бета", "β", new SimpleElement("β"), false));

        autoSimple.add(new AutoCompletion("γ", "gamma", "γ", new SimpleElement("γ"), false));
        autoSimple.add(new AutoCompletion("γ", "гамма", "γ", new SimpleElement("γ"), false));

        autoSimple.add(new AutoCompletion("δ", "delta", "δ", new SimpleElement("δ"), false));
        autoSimple.add(new AutoCompletion("δ", "дельта", "δ", new SimpleElement("δ"), false));

        autoSimple.add(new AutoCompletion("π", "pi", "π", new SimpleElement("π"), false));
        autoSimple.add(new AutoCompletion("π", "пи", "π", new SimpleElement("π"), false));

        autoSimple.add(new AutoCompletion("∞", "infinity", "∞", new SimpleElement("∞"), false));
        autoSimple.add(new AutoCompletion("∞", "бесконечность", "∞", new SimpleElement("∞"), false));

        autoFunction.add(new AutoCompletion("arcsin", "arcsin", "arcsin", new FunctionElement("arcsin"), false));
        autoFunction.add(new AutoCompletion("sin", "sin", "sin", new FunctionElement("sin"), false));
        autoFunction.add(new AutoCompletion("cos", "cos", "cos", new FunctionElement("sin"), false));

        autoNew.add(new AutoCompletion("root", "root", "root", new RootElement(new Formula(true)), true));
        autoNew.add(new AutoCompletion("arcsin", "arcsin", "arcsin", new FunctionElement("arcsin"), true));
        autoNew.add(new AutoCompletion("sin", "sin", "sin", new FunctionElement("sin"), true));
        autoNew.add(new AutoCompletion("cos", "cos", "cos", new FunctionElement("sin"), true));

        autoNew.add(new AutoCompletion("≤", "lessorequal", "≤", new OperatorElement("≤"), true));
        autoNew.add(new AutoCompletion("≤", "меньшеилиравно", "≤", new OperatorElement("≤"), true));

        autoNew.add(new AutoCompletion("≥", "greaterorequal", "≥", new OperatorElement("≥"), true));
        autoNew.add(new AutoCompletion("≥", "большеилиравно", "≥", new OperatorElement("≥"), true));
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
        if (isAutoCompletion) {
            findAutoCompletions();
        }
    }

    protected Rectangle findMaxRect() {
        return formulas.get(formula);
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
        selecting = false;
        canMakeSelection = false;
        //return setCursor(cursor.getItem().getUp(cursor.getPosition()));
        cursor.moveUp();
        redraw();
        return true;
    }

    public boolean moveCursorDown() {
        selecting = false;
        canMakeSelection = false;
        //return setCursor(cursor.getItem().getDown(cursor.getPosition()));
        cursor.moveDown();
        redraw();
        return true;
    }

    public boolean moveCursorLeft() {
        selecting = false;
        canMakeSelection = false;
        //return setCursor(cursor.getItem().getLeft(cursor.getPosition()));
        cursor.moveLeft();
        redraw();
        return true;
    }

    public boolean moveCursorRight() {
        selecting = false;
        canMakeSelection = false;
        //return setCursor(cursor.getItem().getRight(cursor.getPosition()));
        cursor.moveRight();
        redraw();
        return true;
    }

    public boolean moveCursorToPower() {
        selecting = false;
        canMakeSelection = false;
        if (cursor.getItem() instanceof PoweredElement) {
            return moveCursorUp();
        }
        return false;
    }

    public void selectLeft() {
        if (!selecting) {
            selecting = true;
            canMakeSelection = true;
            cursorFrom = cursor.makeClone();
            populateFixer();
        }
        cursor.moveLeft();
        redraw();
    }

    public void selectRight() {
        if (!selecting) {
            selecting = true;
            canMakeSelection = true;
            cursorFrom = cursor.makeClone();
            populateFixer();
        }
        cursor.moveRight();
        redraw();
    }

    public void selectUp() {
        if (!selecting) {
            selecting = true;
            canMakeSelection = true;
            cursorFrom = cursor.makeClone();
            populateFixer();
        }
        cursor.moveUp();
        redraw();
    }

    public void selectDown() {
        if (!selecting) {
            selecting = true;
            canMakeSelection = true;
            cursorFrom = cursor.makeClone();
            populateFixer();
        }
        cursor.moveDown();
        redraw();
    }

    public void insertElement(FormulaItem newItem) {
        Command commandDelSel = Command.ZERO_COMMAND;
        if (selected) {
            commandDelSel = buildDeleteSelection();
            setCursor(commandDelSel.execute());
        }

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
                final Command commandInsert = currentItem.buildInsert(cursor, newItem, fixer);
                setCursor(commandInsert.execute());
                if (commandDelSel == Command.ZERO_COMMAND) {
                    undoer.add(commandInsert);
                } else {
                    final Command command1 = commandDelSel;
                    undoer.add(new Command() {

                        public Cursor execute() {
                            command1.execute();
                            return commandInsert.execute();
                        }

                        public void undo() {
                            commandInsert.undo();
                            command1.undo();
                        }
                    });
                }
                break;

            case BREAK:
                final Command commandBreakWith = currentItem.buildBreakWith(cursor, newItem, fixer);
                setCursor(commandBreakWith.execute());
                if (commandDelSel == Command.ZERO_COMMAND) {
                    undoer.add(commandBreakWith);
                } else {
                    final Command command1 = commandDelSel;
                    undoer.add(new Command() {

                        public Cursor execute() {
                            command1.execute();
                            return commandBreakWith.execute();
                        }

                        public void undo() {
                            commandBreakWith.undo();
                            command1.undo();
                        }
                    });
                }

                command = newItem.buildIncorporateLeft(fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateRight(fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            case LEFT:
                final Command commandInsertBefore = currentItem.buildInsertBefore(newItem, fixer);
                setCursor(commandInsertBefore.execute());
                if (commandDelSel == Command.ZERO_COMMAND) {
                    undoer.add(commandInsertBefore);
                } else {
                    final Command command1 = commandDelSel;
                    undoer.add(new Command() {

                        public Cursor execute() {
                            command1.execute();
                            return commandInsertBefore.execute();
                        }

                        public void undo() {
                            commandInsertBefore.undo();
                            command1.undo();
                        }
                    });
                }

                command = newItem.buildIncorporateLeft(fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateRight(fixer);
                setCursor(command.execute());
                undoer.add(command);
                break;

            case RIGHT:
                final Command commandInsertAfter = currentItem.buildInsertAfter(newItem, fixer);
                setCursor(commandInsertAfter.execute());
                if (commandDelSel == Command.ZERO_COMMAND) {
                    undoer.add(commandInsertAfter);
                } else {
                    final Command command1 = commandDelSel;
                    undoer.add(new Command() {

                        public Cursor execute() {
                            command1.execute();
                            return commandInsertAfter.execute();
                        }

                        public void undo() {
                            commandInsertAfter.undo();
                            command1.undo();
                        }
                    });
                }

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

    public void insert(char c) {
        selecting = false;
        canMakeSelection = false;

        if (c == '+') {
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
            showAutoCompletion();
        }
    }

    public void deleteLeft() {
        selecting = false;
        canMakeSelection = false;
        final Command command1 = buildDeleteSelection();
        setCursor(command1.execute());

        final Command command2 = cursor.getItem().buildDeleteLeft(cursor, fixer);
        setCursor(command2.execute());

        Command command = new Command() {

            public Cursor execute() {
                command1.execute();
                return command2.execute();
            }

            public void undo() {
                command2.undo();
                command1.undo();
            }
        };

        undoer.add(command);

        redraw();
    }

    public void deleteRight() {
        selecting = false;
        canMakeSelection = false;
        final Command command1 = buildDeleteSelection();
        setCursor(command1.execute());

        final Command command2 = cursor.getItem().buildDeleteRight(cursor, fixer);
        setCursor(command2.execute());

        Command command = new Command() {

            public Cursor execute() {
                command1.execute();
                return command2.execute();
            }

            public void undo() {
                command2.undo();
                command1.undo();
            }
        };

        undoer.add(command);

        redraw();
    }

    protected Command buildLightInsertElement(Cursor currentCursor, FormulaItem newItem) {
        if (newItem == null || currentCursor == null) {
            return Command.ZERO_COMMAND;
        }

        FormulaItem currentItem = currentCursor.getItem();
        if (currentItem == null) {
            return Command.ZERO_COMMAND;
        }

        switch (currentItem.getHowToInsert(currentCursor, newItem)) {
            case INSERT:
                return currentItem.buildInsert(currentCursor, newItem, fixer);

            case BREAK:
                return currentItem.buildBreakWith(currentCursor, newItem, fixer);

            case LEFT:
                return currentItem.buildInsertBefore(newItem, fixer);

            case RIGHT:
                return currentItem.buildInsertAfter(newItem, fixer);

            default:
        }
        return Command.ZERO_COMMAND;
    }

    protected Command buildDeleteSelection() {
        if (!selected) {
            return Command.ZERO_COMMAND;
        }
        selected = false;

        final Formula parent_backup = selectedParent;
        final int deletedFrom = selectedPosFrom;
        final List<FormulaItem> deletedItems = new ArrayList<FormulaItem>();

        for (int pos = selectedPosTo; pos >= selectedPosFrom; pos--) {
            FormulaItem item = selectedParent.getItem(pos);
            item.highlightOff();
            deletedItems.add(item);
        }

        return new Command() {

            boolean deleted = false;

            public Cursor execute() {
                if (!deleted) {
                    deleted = true;

                    for (FormulaItem item : deletedItems) {
                        Cursor cursor = parent_backup.getRight(item);
                        parent_backup.remove(item);
                        fixer.removed(item, cursor);
                    }
                }

                FormulaItem item = selectedParent.getItem(deletedFrom);
                if (item != null) {
                    return item.getFirst();
                } else {
                    return selectedParent.getFirst();
                }
            }

            public void undo() {
                if (!deleted) {
                    return;
                }

                deleted = false;
                //int pos = deletedFrom;
                for (FormulaItem item : deletedItems) {
                    parent_backup.insertAt(deletedFrom, item);
                    //pos++;
                }
            }
        };
    }
    protected final List<FormulaItem> copiedItems = new ArrayList<FormulaItem>();

    public void cut() {
        selecting = false;
        canMakeSelection = false;
        copy();
        Command command = buildDeleteSelection();
        setCursor(command.execute());
        undoer.add(command);
        redraw();
    }

    public void copy() {
        selecting = false;
        canMakeSelection = false;
        copiedItems.clear();
        for (int pos = selectedPosFrom; pos <= selectedPosTo; pos++) {
            FormulaItem item = selectedParent.getItem(pos).makeClone();
            item.setParent(null);
            copiedItems.add(item);
        }
    }

    public void paste() {
        selecting = false;
        canMakeSelection = false;

        final List<Command> commands = new ArrayList<Command>(); //Lisy of commands

        if (selected) {
            Command command = buildDeleteSelection();
            if (command != Command.ZERO_COMMAND) {
                setCursor(command.execute());
                commands.add(command);
            }
        }

        //Make list of insertion commands
        for (FormulaItem item : copiedItems) {
            FormulaItem newItem = item.makeClone();
            Command command = buildLightInsertElement(cursor, newItem);
            if (command != Command.ZERO_COMMAND) {
                //Execute commands here and use grandCommand only to undo/redo.
                if (newItem instanceof SimpleElement) {
                    setCursor(command.execute());
                } else {
                    command.execute();
                    setCursor(newItem.getLast());
                }
                commands.add(command);
            }
        }

        //Make one command that executes all insertion commands from the list
        if (!commands.isEmpty()) {
            Command grandCommand = new Command() {

                public Cursor execute() {
                    Cursor cursor = null;
                    for (Command command : commands) {
                        cursor = command.execute();
                    }
                    return cursor;
                }

                public void undo() {
                    for (int i = commands.size() - 1; i >= 0; i--) {
                        commands.get(i).undo();
                    }
                }
            };
            undoer.add(grandCommand);
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

        //Find nearest common parent
        Formula parent = parentFrom;
        int itemPosFrom = parentFrom.getItemPosition(itemFrom);
        int itemPosTo = parentTo.getItemPosition(itemTo);
        if (parentFrom != parentTo) { //Items aren't in one sub-formula
            while (true) {
                if (parentFrom != null) {
                    int pos = parentFrom.posInsideYou(itemTo);
                    if (pos >= 0) {//itemTo is inside itemFroms parent
                        itemPosFrom = parentFrom.posInsideYou(itemFrom);
                        itemPosTo = pos;
                        parent = parentFrom;
                        break;
                    }

                    FormulaItem item = parentFrom.getParent();
                    parentFrom = (item != null) ? item.getParent() : null;
                } else if (parentTo != null) {
                    int pos = parentTo.posInsideYou(itemFrom);
                    if (pos >= 0) {//itemFrom is inside itemTos parent
                        itemPosTo = parentTo.posInsideYou(itemTo);
                        itemPosFrom = pos;
                        parent = parentTo;
                        break;
                    }

                    FormulaItem item = parentTo.getParent();
                    parentTo = (item != null) ? item.getParent() : null;
                } else { //Items aren't in one formula
                    return false;
                }
            }

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
            //canMakeSelection = markSelection(cursorFrom, cursor);
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

    protected final class AutoCompletion {

        private final String showText;
        private String findText;
        private final String replaceWithText;
        private final FormulaItem newItem;
        private final boolean forNew;

        public AutoCompletion(String showText, String findText, String replaceWithText, FormulaItem newItem, boolean forNew) {
            this.showText = showText;
            this.findText = findText;
            this.replaceWithText = replaceWithText;
            this.newItem = newItem;
            this.forNew = forNew;
        }

        public boolean isForNew() {
            return forNew;
        }

        public String getFindText() {
            return findText;
        }

        public void setFindText(String findText) {
            this.findText = findText;
        }

        public FormulaItem getNewItem() {
            return newItem;
        }

        public String getReplaceWithText() {
            return replaceWithText;
        }

        public String getShowText() {
            return showText;
        }

        public AutoCompletion makeClone() {
            return new AutoCompletion(showText, findText, replaceWithText, newItem, forNew);
        }

        public String match(String text) {
            int size = Math.min(findText.length(), text.length());
            int iText = text.length() - size;
            for (int i = size; i > 0; i--) {
                String find = findText.substring(0, i);
                if (find.equals(text.substring(iText))) {
                    return find;
                }
                iText++;
            }
            return "";
        }
    }
    protected boolean isAutoCompletion = false;
    protected int autoCompletionPos = 0;
    protected final List<AutoCompletion> autoFound = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoNew = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoSimple = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoFunction = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoOperator = new ArrayList<AutoCompletion>();

    protected List<AutoCompletion> findAuto(String text, List<AutoCompletion> list) {
        List<AutoCompletion> found = new ArrayList<AutoCompletion>();

        Map<String, AutoCompletion> map = new HashMap<String, AutoCompletion>();
        Set<String> alreadyFound = new TreeSet<String>(new Comparator<String>() {

            public int compare(String o1, String o2) {
                if (o1.length() > o2.length()) {
                    return -1;
                }
                if (o1.length() < o2.length()) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });
        for (AutoCompletion auto : list) {
            String foundText = auto.match(text);
            if (foundText.length() > 0) {
                if (!alreadyFound.contains(foundText)) {
                    AutoCompletion foundAuto = auto.makeClone();
                    foundAuto.setFindText(foundText);
                    map.put(foundText, foundAuto);
                    alreadyFound.add(foundText);
                }
            }
        }

        for (String foundText : alreadyFound) {
            found.add(map.get(foundText));
        }

        /*
        List<String> alreadyFound = new ArrayList<String>();
        for (AutoCompletion auto : list) {
        String foundText = auto.match(text);
        if (foundText.length() > 0) {
        if (!alreadyFound.contains(foundText)) {
        AutoCompletion foundAuto = auto.makeClone();
        foundAuto.setFindText(foundText);
        found.add(foundAuto);
        alreadyFound.add(foundText);
        }
        }
        }
         */

        return found;
    }

    protected Cursor getAutoCompletionCursor() {
        if (cursor == null) {
            return null;
        }

        FormulaItem currentItem = cursor.getItem();
        int pos = cursor.getPosition();
        if (currentItem == null) {
            return null;
        }

        if (pos == 0) {
            Formula parent = currentItem.getParent();
            if (parent == null) {
                return null;
            }

            currentItem = parent.getLeftItem(currentItem);
            if (currentItem == null) {
                return null;
            }

            Cursor curs = currentItem.getLast();
            if (curs == null) {
                return null;
            }
            pos = curs.getPosition();
        }

        return currentItem.getCursor(pos);
    }

    protected int findAutoCompletions() {
        autoFound.clear();

        Cursor curs = getAutoCompletionCursor();
        if (curs == null) {
            isAutoCompletion = false;
            return 0;
        }

        FormulaItem currentItem = curs.getItem();
        int pos = curs.getPosition();
        if (currentItem == null) {
            isAutoCompletion = false;
            return 0;
        }

        if (currentItem instanceof SimpleElement) {
            SimpleElement item = (SimpleElement) currentItem;
            String text = item.getName().substring(0, pos);
            autoFound.addAll(findAuto(text, autoSimple));
            autoFound.addAll(findAuto(text, autoNew));
        } else if (currentItem instanceof FunctionElement) {
            FunctionElement item = (FunctionElement) currentItem;
            String text = item.getName().substring(0, pos);
            autoFound.addAll(findAuto(text, autoFunction));
        } else if (currentItem instanceof OperatorElement) {
            OperatorElement item = (OperatorElement) currentItem;
            String text = item.getName().substring(0, pos);
            autoFound.addAll(findAuto(text, autoOperator));
        }

        int size = autoFound.size();
        if (autoCompletionPos >= size) {
            autoCompletionPos = size;
        }

        if (size <= 0) {
            isAutoCompletion = false;
        }

        return size;
    }

    /**
     *
     * @return true - if editor is showing auto-completion selection box right now
     */
    public boolean isAutoCompletion() {
        return isAutoCompletion;
    }

    /**
     * Show auto-completion selection box
     */
    public void showAutoCompletion() {
        if (!isAutoCompletion) {
            if (findAutoCompletions() > 0) {
                isAutoCompletion = true;
                autoCompletionPos = 0;
                redraw();
            }
        }
    }

    /**
     * Hide auto-completion selection box
     */
    public void hideAutoCompletion() {
        if (isAutoCompletion) {
            isAutoCompletion = false;
            redraw();
        }
    }

    /**
     * Build text part replacement command
     * @param currentItem item to change
     * @param text text to replace with
     * @param pos position in original text to start replacement from
     * @param size size of a chunk of original text to be replaced
     * @return Command object that have been built
     */
    protected Command buildReplace(FormulaItem currentItem, final String text, int pos, final int size) {
        if (currentItem instanceof SimpleElement) {
            final SimpleElement item = (SimpleElement) currentItem;
            final String oldVal = item.getName();
            final int fromPos = pos - size;
            return new Command() {

                public Cursor execute() {
                    item.replacePart(text, fromPos, size);
                    return new Cursor(item, fromPos + text.length());
                }

                public void undo() {
                    item.setName(oldVal);
                }
            };
        } else if (currentItem instanceof FunctionElement) {
            final FunctionElement item = (FunctionElement) currentItem;
            final String oldVal = item.getName();
            final int fromPos = pos - size;
            return new Command() {

                public Cursor execute() {
                    item.replacePart(text, fromPos, size);
                    return new Cursor(item, fromPos + text.length());
                }

                public void undo() {
                    item.setName(oldVal);
                }
            };
        } else if (currentItem instanceof OperatorElement) {
            final OperatorElement item = (OperatorElement) currentItem;
            final String oldVal = item.getName();
            final int fromPos = pos - size;
            return new Command() {

                public Cursor execute() {
                    item.replacePart(text, fromPos, size);
                    return new Cursor(item, fromPos + text.length());
                }

                public void undo() {
                    item.setName(oldVal);
                }
            };
        } else {
            return Command.ZERO_COMMAND;
        }
    }

    /**
     * Use currently selected item from auto-completion selection box
     */
    public void selectAutoCompletion() {
        selecting = false;
        canMakeSelection = false;

        if (!isAutoCompletion) {
            return;
        }
        isAutoCompletion = false;

        AutoCompletion auto = autoFound.get(autoCompletionPos);
        if (auto == null) {
            return;
        }

        Cursor curs = getAutoCompletionCursor();
        if (curs == null) {
            return;
        }

        FormulaItem currentItem = curs.getItem();
        int pos = curs.getPosition();
        if (currentItem == null) {
            return;
        }

        if (auto.isForNew()) {
            if (currentItem instanceof SimpleElement) {
                SimpleElement item = (SimpleElement) currentItem;
                FormulaItem newItem = auto.getNewItem().makeClone();

                final Command command1 = buildReplace(currentItem, "", pos, auto.getFindText().length());
                setCursor(command1.execute());
                final Command command2 = item.buildBreakWith(cursor, newItem, fixer);
                setCursor(command2.execute());

                Command command = new Command() {

                    public Cursor execute() {
                        command1.execute();
                        return command2.execute();
                    }

                    public void undo() {
                        command2.undo();
                        command1.undo();
                    }
                };
                undoer.add(command);

                command = newItem.buildIncorporateLeft(fixer);
                setCursor(command.execute());
                undoer.add(command);

                command = newItem.buildIncorporateRight(fixer);
                setCursor(command.execute());
                undoer.add(command);
            }
        } else {
            Command command = buildReplace(currentItem, auto.getReplaceWithText(), pos, auto.getFindText().length());
            setCursor(command.execute());
            undoer.add(command);
        }

        redraw();
    }

    /**
     * Move auto-completion selection cursor up
     */
    public void moveAutoCompletionUp() {
        if (autoCompletionPos > 0) {
            autoCompletionPos--;
            redraw();
        }
    }

    /**
     * Move auto-completion selection cursor down
     */
    public void moveAutoCompletionDown() {
        if (autoCompletionPos < autoFound.size()) {
            autoCompletionPos++;
            redraw();
        }
    }
}
