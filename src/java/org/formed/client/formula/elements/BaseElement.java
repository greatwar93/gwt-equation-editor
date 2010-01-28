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
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public abstract class BaseElement implements FormulaItem {

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

    public Formula getParent() {
        return parent;
    }

    public void setParent(Formula parent) {
        this.parent = parent;
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

        if(highlighted){
            drawer.fillRect(x, y-metrics.getHeightUp(), x+metrics.getWidth(), y+metrics.getHeightDown(), highlightR, highlightG, highlightB);
        }

        drawer.drawText(val, size, x, y);

        if(strokeThrough){
            drawer.drawLine(x, y+metrics.getHeightDown(), x+metrics.getWidth(), y-metrics.getHeightUp());
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

    public Cursor getFirst() {
        if (getLength() > 0 && parent != null) {
            if (!parent.isFirst(this)) {
                return getCursor(1);
            }
        }
        return getCursor(0);
    }

    public Cursor getLast() {
        return getCursor(getLength());
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

    public Cursor insertChar(Cursor cursor, char c) {
        return insertChar(cursor.getPosition(), c);
    }

    public Cursor insertChar(int pos, char c) {
        if (parent == null) {
            return null;
        }
        FormulaItem item = new SimpleElement("" + c);
        if (pos == 0) {
            parent.insertBefore(item, this);
        } else {
            parent.insertAfter(item, this);
        }
        return item.getLast();
    }

    public Cursor insertChar(int pos, FormulaItem item) {
        if (parent == null) {
            return null;
        }
        if (pos == 0) {
            parent.insertBefore(item, this);
        } else {
            parent.insertAfter(item, this);
        }
        return item.getLast();
    }

    public Cursor removeChar(int pos) {
        if (parent == null) {
            return null;
        }
        if (pos == 0) {
            parent.removeLeft(this);
            return getFirst();
        } else {
            parent.removeRight(this);
            return getLast();
        }
    }
    /*
    public Cursor deleteLeft(Drawer drawer, Cursor cursor) {
    if (parent == null) {
    return cursor;
    }

    if (cursor.getPosition() <= 0) {
    return parent.removeLeft(drawer, this);
    }

    Cursor newCursor = parent.getLeft(drawer, this);
    parent.remove(this);

    if (newCursor == null) {
    newCursor = parent.getFirst(drawer);
    }

    return newCursor;
    }

    public Cursor deleteRight(Drawer drawer, Cursor cursor) {
    if (parent == null) {
    return cursor;
    }

    if (cursor.getPosition() > 0) {
    return parent.removeRight(drawer, this);
    }

    Cursor newCursor = parent.getRight(drawer, this);
    parent.remove(this);

    if (newCursor == null) {
    newCursor = parent.getLast(drawer);
    }

    return newCursor;
    }
     */

    public Command deleteLeft(final Cursor cursor) {
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem THIS = this;
        final Formula parent_backup = parent;

        if (cursor.getPosition() <= 0) { //Remove item to the left
            final FormulaItem left = parent_backup.getLeftItem(THIS);
            if(left == null) return Command.ZERO_COMMAND;

            return new Command() {

                public Cursor execute() {
                    return parent_backup.removeLeft(THIS);
                }

                public void undo() {
                    parent_backup.insertBefore(left, THIS);
                }
            };
        } else { //Remove this item
            return new Command() {
                final int pos = parent_backup.getItemPosition(THIS);

                public Cursor execute() {
                    Cursor newCursor = parent_backup.getLeft(THIS);
                    parent_backup.remove(THIS);

                    if (newCursor == null) {
                        newCursor = parent_backup.getFirst();
                    }

                    return newCursor;
                }

                public void undo() {
                    parent_backup.insertAt(pos, THIS);
                }
            };
        }
    }


    public Command deleteRight(final Cursor cursor) {
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem THIS = this;
        final Formula parent_backup = parent;

        if (cursor.getPosition() > 0) { //Remove item to the right
            final FormulaItem right = parent_backup.getRightItem(THIS);
            if(right == null) return Command.ZERO_COMMAND;

            return new Command() {

                public Cursor execute() {
                    return parent_backup.removeRight(THIS);
                }

                public void undo() {
                    parent_backup.insertAfter(right, THIS);
                }
            };
        } else { //Remove this item
            return new Command() {
                final int pos = parent_backup.getItemPosition(THIS);

                public Cursor execute() {
                    Cursor newCursor = parent_backup.getRight(THIS);
                    parent_backup.remove(THIS);

                    if (newCursor == null) {
                        newCursor = parent_backup.getLast();
                    }

                    return newCursor;
                }

                public void undo() {
                    parent_backup.insertAt(pos, THIS);
                }
            };
        }
    }

}
