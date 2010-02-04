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
package org.formed.client.formula.elements;

import org.formed.client.formula.Command;
import org.formed.client.formula.Cursor;
import org.formed.client.formula.CursorFixer;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public abstract class BaseElement implements FormulaItem {

    protected final FormulaItem THIS = this;
    protected Formula parent = null;
    protected String val = "";
    protected int storedSize = 0;
    protected int storedX = 0;
    protected int storedY = 0;
    protected boolean metricsValid = false;
    protected Metrics storedMetrics;
    protected boolean strokeThrough = false;
    protected boolean highlighted = false;
    protected int highlightR = 255;
    protected int highlightG = 255;
    protected int highlightB = 255;

    public BaseElement() {
    }

    public BaseElement(boolean strokeThrough) {
        this.strokeThrough = strokeThrough;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Formula getParent() {
        return parent;
    }

    public Formula setParent(Formula parent) {
        Formula parent_backup = this.parent;
        this.parent = parent;
        return parent_backup;
    }

    public Formula removeFromParent() {
        Formula parent_backup = parent;
        if (parent != null) {
            parent.remove(this);
            parent = null;
        }
        return parent_backup;
    }

    public boolean isLeftCloser() {
        return false;
    }

    public boolean isRightCloser() {
        return false;
    }

    public boolean isIncorporatable() {
        return true;
    }

    public boolean isEmpty() {
        return val.isEmpty();
    }

    public boolean isYouOrInsideYou(FormulaItem item) {
        return this == item;
    }

    public void setStrokeThrough(boolean strokeThrough) {
        this.strokeThrough = strokeThrough;
    }

    public void highlightOff() {
        highlighted = false;
    }

    public void setHighlight(int r, int g, int b) {
        highlighted = true;
        highlightR = r;
        highlightG = g;
        highlightB = b;
    }

    public Metrics draw(Drawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        Metrics metrics = drawer.textMetrics(val, size);

        if (highlighted) {
            drawer.fillRect(x, y - metrics.getHeightUp(), x + metrics.getWidth(), y + metrics.getHeightDown(), highlightR, highlightG, highlightB);
        }

        drawer.drawText(val, size, x, y);

        if (strokeThrough) {
            drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
        }

        drawer.addDrawnItem(this, x, y, metrics);

        return metrics;
    }

    public Metrics measure(Drawer drawer, int size) {
        storedSize = size;

        return drawer.textMetrics(val, size);
    }

    protected int getLength() {
        return val.length();
    }

    public void replacePart(String replaceWith, int pos, int size) {
        val = val.substring(0, pos) + replaceWith + val.substring(pos + size);
    }

    private String getPart(int position) {
        return val.substring(0, position);
    }

    public Cursor getCursor(Drawer drawer, int x, int y) {
        int dx = x - storedX;

        int width = 0;
//        Metrics metrics = drawer.textMetrics(val.substring(0, 1), storedSize);
        Metrics metrics = drawer.textMetrics(getPart(1), storedSize);
        for (int i = 1; i <= getLength(); i++) {
            Metrics newMetrics = drawer.textMetrics(getPart(i), storedSize);
//            Metrics newMetrics = drawer.textMetrics(val.substring(0, i), storedSize);
            int newWidth = newMetrics.getWidth();

            if (newWidth > dx) {
                if ((newWidth - dx) > (newWidth - width) / 2) {
                    return new Cursor(this, i - 1, storedX + width, storedY, metrics.getHeightUp(), metrics.getHeightDown());
                } else {
                    return new Cursor(this, i, storedX + newWidth, storedY, newMetrics.getHeightUp(), newMetrics.getHeightDown());
                }
            }

            width = newWidth;
            metrics = newMetrics;
        }

        Metrics zeroMetrics = drawer.textMetrics("0", storedSize);
        return new Cursor(this, 0, storedX + width, storedY, zeroMetrics.getHeightUp(), zeroMetrics.getHeightDown());
    }

    public Cursor getLeft(int oldPosition) {
        int position = oldPosition - 1;

        if (position == 0) {
            if (parent != null) {
                if (!parent.isFirst(this)) {
                    return parent.getLeft(this);
                }
            }
        } else if (position < 0) {
            if (parent == null) {
                return null;
            }

            return parent.getLeft(this);
        }

        return getCursor(position);
    }

    public Cursor getRight(int oldPosition) {
        int position = oldPosition + 1;
        if (position > getLength()) {
            if (parent == null) {
                return null;
            }

            return parent.getRight(this);
        }

        return getCursor(position);
    }

    public Cursor getUp(int oldPosition) {
        if (parent == null) {
            return null;
        }
        return parent.getUp(this);
    }

    public Cursor getDown(int oldPosition) {
        if (parent == null) {
            return null;
        }
        return parent.getDown(this);
    }

    public void reMeasureCursor(Drawer drawer, Cursor cursor) {
//        cursor.setCursor(getCursor(drawer, cursor.getPosition()));

        Metrics newMetrics = drawer.textMetrics(getPart(cursor.getPosition()), storedSize);
        cursor.setX(storedX + newMetrics.getWidth());
        cursor.setY(storedY);
        cursor.setHeightUp(newMetrics.getHeightUp());
        cursor.setHeightDown(newMetrics.getHeightDown());
    }

    public Cursor getCursor(int position) {
        return new Cursor(this, position);
    }

    public Cursor getMovementFirst() {
        if (getLength() > 0 && parent != null) {
            if (!parent.isFirst(this)) {
                return getCursor(1);
            }
        }
        return getCursor(0);
    }

    public Cursor getFirst() {
        return getCursor(0);
    }

    public Cursor getLast() {
        return getCursor(getLength());
    }

    public Cursor getEditPlace() {
        return getCursor(1);
    }

    public Cursor childAsksLeft(Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getLeft(this);
    }

    public Cursor childAsksRight(Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getRight(this);
    }

    public Cursor childAsksUp(Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getUp(this);
    }

    public Cursor childAsksDown(Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getDown(this);
    }

    public void invalidatePlaces(Formula source) {
        if (parent != source && parent != null) {
            parent.invalidatePlaces(this);
        }
    }

    public void invalidateMetrics() {
        metricsValid = false;
    }

    public Command buildSimpleDeleteLeft(final Cursor cursor, final CursorFixer fixer) {
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;

        if (cursor.getPosition() <= 0) { //Remove item to the left
            FormulaItem left = parent_backup.getLeftItem(this);
            if (left == null || left == this) {
                return Command.ZERO_COMMAND;
            }

            return left.buildDeleteLeft(left.getLast(), fixer);
        } else { //Remove this item
            return new Command() {

                final int pos = parent_backup.getItemPosition(THIS);

                public Cursor execute() {
                    Cursor newCursor = parent_backup.getLeft(THIS);
                    parent_backup.remove(THIS);

                    if (newCursor == null) {
                        newCursor = parent_backup.getFirst();
                    }

                    fixer.removed(THIS, newCursor);

                    return newCursor;
                }

                public void undo() {
                    parent_backup.insertAt(pos, THIS);
                }
            };
        }
    }

    public Command buildSimpleDeleteRight(final Cursor cursor, final CursorFixer fixer) {
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;

        if (cursor.getPosition() > 0) { //Remove item to the right
            final FormulaItem right = parent_backup.getRightItem(this);
            if (right == null || right == this) {
                return Command.ZERO_COMMAND;
            }

            return right.buildDeleteRight(right.getFirst(), fixer);
        } else { //Remove this item
            return new Command() {

                final int pos = parent_backup.getItemPosition(THIS);

                public Cursor execute() {
                    Cursor newCursor = parent_backup.getRight(THIS);
                    parent_backup.remove(THIS);

                    if (newCursor == null) {
                        newCursor = parent_backup.getLast();
                    }

                    fixer.removed(THIS, newCursor);

                    return newCursor;
                }

                public void undo() {
                    parent_backup.insertAt(pos, THIS);
                }
            };
        }
    }

    @Override
    public Command buildDeleteLeft(Cursor cursor, final CursorFixer fixer) {
        final int pos = cursor.getPosition();
        final Cursor newCursor = cursor.makeClone();
        final Formula parent_backup = parent;
        if (pos <= 0) { //Delete in adjecent item
            FormulaItem left = parent_backup.getLeftItem(this);
            if (left == null || left == this) {
                return Command.ZERO_COMMAND;
            }

            return left.buildDeleteLeft(left.getLast(), fixer);
        }

        //Delete char
        final String oldVal = val;
        newCursor.setPosition(pos - 1);
        return new Command() {

            boolean reInsert = false;
            int reInsertPos = 0;

            public Cursor execute() {
                val = val.substring(0, pos - 1) + val.substring(pos);

                if (isEmpty()) { //This item is empty now, remove it
                    reInsert = true;
                    reInsertPos = parent_backup.getItemPosition(THIS);
                    parent_backup.remove(THIS);

                    FormulaItem item = parent_backup.getItem(reInsertPos); //Try next item
                    if (item != null) {
                        newCursor.setCursor(item.getFirst());
                    } else {
                        item = parent_backup.getItem(reInsertPos - 1); //Try previous item
                        if (item != null) {
                            newCursor.setCursor(item.getLast());
                        } else {
                            newCursor.setCursor(parent_backup.getFirst());
                        }
                    }

                    fixer.removed(THIS, newCursor);
                } else {
                    reInsert = false;
                }

                return newCursor;
            }

            public void undo() {
                val = oldVal;
                if (reInsert) { //Re insert this item
                    parent_backup.insertAt(reInsertPos, THIS);
                    reInsert = false;
                }
            }
        };
    }

    @Override
    public Command buildDeleteRight(Cursor cursor, final CursorFixer fixer) {
        final int pos = cursor.getPosition();
        final Cursor newCursor = cursor.makeClone();
        final Formula parent_backup = parent;
        if (pos >= val.length()) { //Delete in adjacent item
            final FormulaItem right = parent_backup.getRightItem(this);
            if (right == null || right == this) {
                return Command.ZERO_COMMAND;
            }

            return right.buildDeleteRight(right.getFirst(), fixer);
        }

        //Delete char
        final String oldVal = val;
        return new Command() {

            boolean reInsert = false;
            int reInsertPos = 0;

            public Cursor execute() {
                val = val.substring(0, pos) + val.substring(pos + 1);

                if (THIS.isEmpty()) { //This item is empty now, remove it
                    reInsert = true;
                    reInsertPos = parent_backup.getItemPosition(THIS);
                    parent_backup.remove(THIS);

                    FormulaItem item = parent_backup.getItem(reInsertPos); //Try next item
                    if (item != null) {
                        newCursor.setCursor(item.getFirst());
                    } else {
                        item = parent_backup.getItem(reInsertPos - 1); //Try previous item
                        if (item != null) {
                            newCursor.setCursor(item.getLast());
                        } else {
                            newCursor.setCursor(parent_backup.getFirst());
                        }
                    }

                    fixer.removed(THIS, newCursor);
                } else {
                    reInsert = false;
                }

                return newCursor;
            }

            public void undo() {
                val = oldVal;
                if (reInsert) { //Re insert this item
                    parent_backup.insertAt(reInsertPos, THIS);
                    reInsert = false;
                }
            }
        };
    }

    public HowToInsert getHowToInsert(Cursor cursor, FormulaItem item) {
        return HowToInsert.LEFT;
    }

    public Command buildInsertAfter(final FormulaItem item, final CursorFixer fixer) {
        if (!hasParent() || item == null) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;

        return new Command() {

            public Cursor execute() {
                parent_backup.insertAfter(item, THIS);
                return item.getEditPlace();
            }

            public void undo() {
                parent_backup.remove(item);
                fixer.removed(item, getLast());
            }
        };
    }

    public Command buildInsertBefore(final FormulaItem item, final CursorFixer fixer) {
        if (!hasParent() || item == null) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;

        return new Command() {

            public Cursor execute() {
                parent_backup.insertBefore(item, THIS);
                return item.getEditPlace();
            }

            public void undo() {
                parent_backup.remove(item);
                fixer.removed(item, getMovementFirst());
            }
        };
    }

    public Command buildBreakWith(Cursor cursor, FormulaItem item, final CursorFixer fixer) {
        return Command.ZERO_COMMAND;
    }

    public Cursor insertString(int pos, String s) {
        val = val.substring(0, pos) + s + val.substring(pos);
        invalidatePlaces(null);
        return getCursor(pos + s.length());
    }

    public Cursor undoInsertString(int pos, String s, CursorFixer fixer) {
        val = val.substring(0, pos) + val.substring(pos + s.length());

        if (val.length() <= 0) {
            if (hasParent()) {
                Cursor cursor = parent.getLeft(this);

                Formula parent_backup = removeFromParent(); //this commands clear parent so we need a backup to finish all commands

                if (cursor.getItem() == this) {
                    fixer.removed(this, parent_backup.getFirst());
                    return parent_backup.getFirst();
                } else {
                    fixer.removed(this, cursor);
                    return cursor;
                }
            }
        }

        invalidatePlaces(null);
        return getCursor(pos);
    }

    public Command buildInsert(Cursor cursor, FormulaItem item, final CursorFixer fixer) {
        if (item == null || cursor == null) {
            return Command.ZERO_COMMAND;
        }
        if (!(item instanceof SimpleElement)) {
            return Command.ZERO_COMMAND;
        }

        final int pos = cursor.getPosition();
        final String s = ((SimpleElement) item).getName();

        return new Command() {

            public Cursor execute() {
                return insertString(pos, s);
            }

            public void undo() {
                undoInsertString(pos, s, fixer);
            }
        };
    }

    public Command buildIncorporateLeft(final CursorFixer fixer) {
        return Command.ZERO_COMMAND;
    }

    public Command buildIncorporateRight(final CursorFixer fixer) {
        return Command.ZERO_COMMAND;
    }

    protected Command buildIncorporateRight(final Formula dest, final CursorFixer fixer) {
        if (!hasParent() || dest == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem item = parent.getRightItem(this);
        if (item == null) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;
        if (item.isLeftCloser()) {
            final int posFrom = parent.getItemPosition(item);
            int posTo = parent.findRightCloserPos(posFrom);
            final int size = posTo - posFrom + 1;
            return new Command() {

                public Cursor execute() {
                    parent_backup.moveFormula(posFrom, size, dest, 0);
                    return getLast();
                }

                public void undo() {
                    dest.moveFormula(0, size, parent_backup, posFrom);
                }
            };
        }

        if (!item.isIncorporatable()) {
            return Command.ZERO_COMMAND;
        }
        return new Command() {

            public Cursor execute() {
                parent_backup.remove(item);
                dest.add(item);
                return getLast();
            }

            public void undo() {
                dest.remove(item);
                parent_backup.insertAfter(item, THIS);
            }
        };
    }
}
