/*
Copyright 2010 Bulat Sirazetdinov

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
import org.formed.client.formula.editor.AutoCompletion;
import org.formed.client.formula.editor.Clipboard;
import org.formed.client.formula.editor.Command;
import org.formed.client.formula.editor.Cursor;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Editor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.drawer.Metrics;
import org.formed.client.formula.drawer.Rectangle;
import org.formed.client.formula.editor.Undoer;
import org.formed.client.formula.elements.DivisorElement;
import org.formed.client.formula.elements.FunctionElement;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.PoweredElement;
import org.formed.client.formula.elements.RightCloser;
import org.formed.client.formula.elements.SimpleElement;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class SimpleEditor implements Editor {

    protected final Formula formula;
    protected final Drawer drawer;
    protected final Undoer undoer;
    protected final Clipboard clipboard;
    protected SimpleCursorFixer fixer = new SimpleCursorFixer();
    //Cursors
    protected Cursor cursor = new Cursor(new SimpleElement(""), 0);
    protected Cursor cursorFrom = new Cursor(new SimpleElement(""), 0);
    protected Cursor cursorHighlight = new Cursor(new SimpleElement(""), 0);
    protected FormulaItem highlighted1 = null;
    protected FormulaItem highlighted2 = null;
    //Selection handling
    protected boolean selecting = false;
    protected boolean canMakeSelection = false;
    protected boolean selected = false;
    protected Formula selectedParent = null;
    protected int selectedPosFrom = 0;
    protected int selectedPosTo = 0;
    //Autocompletion handling
    protected boolean isAutoCompletion = false;
    protected int autoCompletionPos = 0;
    protected final List<AutoCompletion> autoFound = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoNew = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoSimple = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoFunction = new ArrayList<AutoCompletion>();
    protected final List<AutoCompletion> autoOperator = new ArrayList<AutoCompletion>();

    public SimpleEditor(Formula formula, Drawer drawer, Undoer undoer, Clipboard clipboard) {
        this.formula = formula;
        this.drawer = drawer;
        this.undoer = undoer;
        this.clipboard = clipboard;

        populateFixer();
    }

    /**
     * Add AutoCompletion objects to be used to create new elements
     * @param list
     */
    public void populateAutoNew(List<AutoCompletion> list) {
        autoNew.addAll(list);
    }

    /**
     * Add AutoCompletion objects to be used to edit SimpleElements
     * @param list A list of objects to be added
     */
    public void populateAutoSimple(List<AutoCompletion> list) {
        autoSimple.addAll(list);
    }

    /**
     * Add AutoCompletion objects to be used to edit FunctionElements
     * @param list A list of objects to be added
     */
    public void populateAutoFunction(List<AutoCompletion> list) {
        autoFunction.addAll(list);
    }

    /**
     * Add AutoCompletion objects to be used to edit OperatorElements
     * @param list A list of objects to be added
     */
    public void populateAutoOperator(List<AutoCompletion> list) {
        autoOperator.addAll(list);
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

    protected void selectionExpired() {
        selecting = false;
        canMakeSelection = false;
    }

    public boolean moveCursorUp() {
        selectionExpired();
        cursor.moveUp();
        redraw();
        return true;
    }

    public boolean moveCursorDown() {
        selectionExpired();
        cursor.moveDown();
        redraw();
        return true;
    }

    public boolean moveCursorLeft() {
        selectionExpired();
        cursor.moveLeft();
        redraw();
        return true;
    }

    public boolean moveCursorRight() {
        selectionExpired();
        cursor.moveRight();
        redraw();
        return true;
    }

    public boolean moveCursorFirst() {
        selectionExpired();
        cursor.moveFirst();
        redraw();
        return true;
    }

    public boolean moveCursorLast() {
        selectionExpired();
        cursor.moveLast();
        redraw();
        return true;
    }

    public boolean moveCursorToPower() {
        selectionExpired();
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
        selectionExpired();

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
        selectionExpired();
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
        selectionExpired();
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

    public void cut() {
        selectionExpired();
        copy();
        Command command = buildDeleteSelection();
        setCursor(command.execute());
        undoer.add(command);
        redraw();
    }

    public void copy() {
        selectionExpired();
        List<FormulaItem> copiedItems = new ArrayList<FormulaItem>();
        for (int pos = selectedPosFrom; pos <= selectedPosTo; pos++) {
            FormulaItem item = selectedParent.getItem(pos).makeClone();
            item.setParent(null);
            copiedItems.add(item);
        }
        clipboard.copy(copiedItems);
    }

    public void paste() {
        selectionExpired();

        final List<Command> commands = new ArrayList<Command>(); //Lisy of commands

        if (selected) {
            Command command = buildDeleteSelection();
            if (command != Command.ZERO_COMMAND) {
                setCursor(command.execute());
                commands.add(command);
            }
        }

        //Make list of insertion commands
        List<FormulaItem> copiedItems = clipboard.paste();
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

    public void selectAll() {
        if (!selecting) {
            selecting = true;
            canMakeSelection = true;
        }
        cursorFrom = formula.getFirst();
        cursor.setCursor(formula.getLast());
        populateFixer();
        redraw();
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
        FormulaItem minRectItem = drawer.findItemAt(x, y);

        if (minRectItem != null) {
            selecting = true;
            canMakeSelection = true;
            cursorFrom = minRectItem.getCursor(drawer, x, y);
            cursor = cursorFrom;
        }

        populateFixer();
        redraw();
    }

    public void mouseUpAt(int x, int y) {
        FormulaItem minRectItem = drawer.findItemAt(x, y);

        if (minRectItem != null) {
            cursor = minRectItem.getCursor(drawer, x, y);
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
            FormulaItem minRectItem = drawer.findItemAt(x, y);

            if (minRectItem != null) {
                selecting = true;
                cursor = minRectItem.getCursor(drawer, x, y);
            }
        } else {
            highlightItemAt(x, y);
        }

        populateFixer();
        redraw();
    }

    public FormulaItem selectItemAt(int x, int y) {
        FormulaItem minRectItem = drawer.findItemAt(x, y);

        if (minRectItem != null) {
            cursor = minRectItem.getCursor(drawer, x, y);
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

        FormulaItem minRectItem = drawer.findItemAt(x, y);

        if (minRectItem != null) {
            cursorHighlight = minRectItem.getCursor(drawer, x, y);

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
        selectionExpired();

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
        } else {
            autoCompletionPos = autoFound.size();
        }
        redraw();
    }

    /**
     * Move auto-completion selection cursor down
     */
    public void moveAutoCompletionDown() {
        if (autoCompletionPos < autoFound.size() - 1) {
            autoCompletionPos++;
        } else {
            autoCompletionPos = 0;
        }
        redraw();
    }

    public Rectangle drawAutoCompletion() {
        if (!isAutoCompletion) {
            return new Rectangle(0, 0, 0, 0);
        }

        int size = autoFound.size();
        if (size <= 0) {
            return new Rectangle(0, 0, 0, 0);
        }

        cursor.measure(drawer);
        int x = cursor.getX();
        int y1 = cursor.getY() + cursor.getHeightDown();
        int y = y1;

        int i = 0;

        //find width
        int maxWidth = 0;
        for (AutoCompletion auto : autoFound) {
            if (auto.isForNew()) {
                FormulaItem item = auto.getNewItem();
                String text = auto.getFindText() + " → ";
                Metrics metrics = drawer.measureText(text, 20);
                Metrics metrics2 = item.measure(drawer, 20);
                maxWidth = Math.max(maxWidth, metrics.getWidth() + metrics2.getWidth());
            } else {
                String text = auto.getFindText() + " → " + auto.getShowText();
                Metrics metrics = drawer.measureText(text, 20);
                maxWidth = Math.max(maxWidth, metrics.getWidth());
            }
        }

        //draw
        for (AutoCompletion auto : autoFound) {
            if (auto.isForNew()) {
                FormulaItem item = auto.getNewItem();

                String text = auto.getFindText() + " → ";
                Metrics metrics = drawer.measureText(text, 20);
                Metrics metrics2 = item.measure(drawer, 20);

                if (i == autoCompletionPos) {
                    item.setHighlight(255, 255, 0);
                    drawer.fillRect(x, y, x + maxWidth, y + Math.max(metrics.getHeight(), metrics2.getHeight()) + 2, 255, 255, 0);
                } else {
                    item.setHighlight(255, 255, 255);
                    drawer.fillRect(x, y, x + maxWidth, y + Math.max(metrics.getHeight(), metrics2.getHeight()) + 2, 255, 255, 255);
                }

                y += Math.max(metrics.getHeightUp(), metrics2.getHeightUp()) + 1;
                drawer.drawText(text, 20, x, y);
                item.draw(drawer, x + metrics.getWidth(), y, 20);
                item.highlightOff();
                y += Math.max(metrics.getHeightDown(), metrics2.getHeightDown()) + 1;

            } else {
                String text = auto.getFindText() + " → " + auto.getShowText();
                Metrics metrics = drawer.measureText(text, 20);

                if (i == autoCompletionPos) {
                    drawer.fillRect(x, y, x + maxWidth, y + metrics.getHeight() + 2, 255, 255, 0);
                } else {
                    drawer.fillRect(x, y, x + maxWidth, y + metrics.getHeight() + 2, 255, 255, 255);
                }

                y += metrics.getHeightUp() + 1;
                drawer.drawText(text, 20, x, y);
                y += metrics.getHeightDown() + 1;
            }

            i++;
        }

        drawer.drawRect(x, y1, x + maxWidth, y);
        return new Rectangle(x, y, maxWidth, y - y1);
    }

    public Rectangle measureAutoCompletion() {
        if (!isAutoCompletion) {
            return new Rectangle(0, 0, 0, 0);
        }

        int size = autoFound.size();
        if (size <= 0) {
            return new Rectangle(0, 0, 0, 0);
        }

        cursor.measure(drawer);
        int x = cursor.getX();
        int y1 = cursor.getY() + cursor.getHeightDown();
        int y = y1;

        int i = 0;

        //find width
        int maxWidth = 0;
        for (AutoCompletion auto : autoFound) {
            if (auto.isForNew()) {
                FormulaItem item = auto.getNewItem();
                String text = auto.getFindText() + " → ";
                Metrics metrics = drawer.measureText(text, 20);
                Metrics metrics2 = item.measure(drawer, 20);
                maxWidth = Math.max(maxWidth, metrics.getWidth() + metrics2.getWidth());
            } else {
                String text = auto.getFindText() + " → " + auto.getShowText();
                Metrics metrics = drawer.measureText(text, 20);
                maxWidth = Math.max(maxWidth, metrics.getWidth());
            }
        }

        //draw
        for (AutoCompletion auto : autoFound) {
            if (auto.isForNew()) {
                FormulaItem item = auto.getNewItem();

                String text = auto.getFindText() + " → ";
                Metrics metrics = drawer.measureText(text, 20);
                Metrics metrics2 = item.measure(drawer, 20);

                y += Math.max(metrics.getHeightUp(), metrics2.getHeightUp()) + 1;
                y += Math.max(metrics.getHeightDown(), metrics2.getHeightDown()) + 1;

            } else {
                String text = auto.getFindText() + " → " + auto.getShowText();
                Metrics metrics = drawer.measureText(text, 20);

                y += metrics.getHeightUp() + 1;
                y += metrics.getHeightDown() + 1;
            }

            i++;
        }

        return new Rectangle(x, y, maxWidth, y - y1);
    }

    public void addFormula(Formula formula, Drawer drawer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Metrics redraw(Drawer drawer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Metrics redraw(Formula formula) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void redrawCursor() {
        if (cursorHighlight != null) {
            drawer.drawDottedLine(cursorHighlight.getX(), cursorHighlight.getY() - cursorHighlight.getHeightUp(), cursorHighlight.getX(), cursorHighlight.getY() + cursorHighlight.getHeightDown());
        }

        if (cursor != null) {
            drawer.drawLine(cursor.getX(), cursor.getY() - cursor.getHeightUp(), cursor.getX(), cursor.getY() + cursor.getHeightDown());
        }
    }

    public void preRedraw() {
        markSelection(cursorFrom, cursor);
        if (isAutoCompletion) {
            findAutoCompletions();
        }
    }

    public void postRedraw() {
        if (cursor != null) {
            cursor.measure(drawer);
        }
        if (cursorHighlight != null) {
            cursorHighlight.measure(drawer);
        }
        redrawCursor();
    }

    public Metrics redraw() {
        Metrics metrics = drawer.measure(formula);
        Rectangle rect = measureAutoCompletion();
        int width = Math.max(10 + metrics.getWidth(), rect.getX() + rect.getWidth()) + 5;
        int height = Math.max(20 + metrics.getHeight(), rect.getY() + rect.getHeight()) + 5;

        metrics.setWidth(width);
        metrics.setHeightUp(0);
        metrics.setHeightDown(height);

        if (width > drawer.getWidth() || height > drawer.getHeight()) {
            drawer.setWidth(width);
            drawer.setHeight(height);
        }

        preRedraw();

        drawer.redraw(formula);
        drawAutoCompletion();

        postRedraw();
        return metrics;
    }
}
