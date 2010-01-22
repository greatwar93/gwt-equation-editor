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

    public BaseElement() {
    }

    public Formula getParent() {
        return parent;
    }

    public void setParent(Formula parent) {
        this.parent = parent;
    }

    public Metrics draw(Drawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        drawer.drawText(val, size, x, y);

        Metrics metrics = drawer.textMetrics(val, size);

        drawer.addDrawnItem(this, x, y, metrics);

        return metrics;
    }

    public Metrics measure(Drawer drawer, int size) {
        storedSize = size;

        return drawer.textMetrics(val, size);
    }

    protected int getLength() {
        return val.length();
        /*        int p = 0;
        boolean escaped = false;
        for (int i = 0; i < val.length(); i++) {
        char c = val.charAt(i);
        if (escaped) {
        if (c == ';') {
        escaped = false;
        }
        } else {
        p++;
        if (c == '&') {
        escaped = true;
        }
        }
        }

        return p;*/
    }

    private String getPart(int position) {
        return val.substring(0, position);
        /*
        StringBuilder s = new StringBuilder();
        int p = 0;
        boolean escaped = false;
        for (int i = 0; i < val.length() && p < position; i++) {
        char c = val.charAt(i);
        if (escaped) {
        s.append(c);
        if (c == ';') {
        p++;
        escaped = false;
        }
        } else {
        s.append(c);
        if (c == '&') {
        escaped = true;
        } else {
        p++;
        }
        }
        }

        return s.toString();*/
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
                    return new Cursor(drawer, this, i - 1, storedX + width, storedY, metrics.getHeightUp(), metrics.getHeightDown());
                } else {
                    return new Cursor(drawer, this, i, storedX + newWidth, storedY, newMetrics.getHeightUp(), newMetrics.getHeightDown());
                }
            }

            width = newWidth;
            metrics = newMetrics;
        }

        Metrics zeroMetrics = drawer.textMetrics("0", storedSize);
        return new Cursor(drawer, this, 0, storedX + width, storedY, zeroMetrics.getHeightUp(), zeroMetrics.getHeightDown());
    }

    public Cursor getLeft(Drawer drawer, int oldPosition) {
        int position = oldPosition - 1;

        if (position == 0) {
            if (parent != null) {
                if (!parent.isFirst(this)) {
                    return parent.getLeft(drawer, this);
                }
            }
        } else if (position < 0) {
            if (parent == null) {
                return null;
            }

            return parent.getLeft(drawer, this);
        }

        return getCursor(drawer, position);
    }

    public Cursor getRight(Drawer drawer, int oldPosition) {
        int position = oldPosition + 1;
        if (position > getLength()) {
            if (parent == null) {
                return null;
            }

            return parent.getRight(drawer, this);
        }

        return getCursor(drawer, position);
    }

    public Cursor getUp(Drawer drawer, int oldPosition) {
        if (parent == null) {
            return null;
        }
        return parent.getUp(drawer, this);
    }

    public Cursor getDown(Drawer drawer, int oldPosition) {
        if (parent == null) {
            return null;
        }
        return parent.getDown(drawer, this);
    }

    public void reMeasureCursor(Drawer drawer, Cursor cursor) {
        cursor.setCursor(getCursor(drawer, cursor.getPosition()));
    }

    public Cursor getCursor(Drawer drawer, int position) {
        Metrics newMetrics = drawer.textMetrics(getPart(position), storedSize);
//        Metrics newMetrics = drawer.textMetrics(val.substring(0, position), storedSize);
/*        if (newMetrics.getHeight() == 0) {
        Metrics zeroMetrics = drawer.textMetrics("0", storedSize);
        newMetrics.setHeightUp(zeroMetrics.getHeightUp());
        newMetrics.setHeightDown(zeroMetrics.getHeightDown());
        }*/
        return new Cursor(drawer, this, position, storedX + newMetrics.getWidth(), storedY, newMetrics.getHeightUp(), newMetrics.getHeightDown());
    }

    public Cursor getFirst(Drawer drawer) {
        if (getLength() > 0 && parent != null) {
            if (!parent.isFirst(this)) {
                return getCursor(drawer, 1);
            }
        }
        return getCursor(drawer, 0);
    }

    public Cursor getLast(Drawer drawer) {
        return getCursor(drawer, getLength());
    }

    public Cursor childAsksLeft(Drawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getLeft(drawer, this);
    }

    public Cursor childAsksRight(Drawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getRight(drawer, this);
    }

    public Cursor childAsksUp(Drawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getUp(drawer, this);
    }

    public Cursor childAsksDown(Drawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getDown(drawer, this);
    }

    public void invalidatePlaces(Formula source) {
        if (parent != source && parent != null) {
            parent.invalidatePlaces(this);
        }
    }

    public void invalidateMetrics() {
        metricsValid = false;
    }

    public Cursor insertChar(Drawer drawer, Cursor cursor, char c) {
        return insertChar(drawer, cursor.getPosition(), c);
    }

    public Cursor insertChar(Drawer drawer, int pos, char c) {
        if (parent == null) {
            return null;
        }
        FormulaItem item = new SimpleElement("" + c);
        if (pos == 0) {
            parent.insertBefore(item, this);
        } else {
            parent.insertAfter(item, this);
        }
        return item.getLast(drawer);
    }

    public Cursor insertChar(Drawer drawer, int pos, FormulaItem item) {
        if (parent == null) {
            return null;
        }
        if (pos == 0) {
            parent.insertBefore(item, this);
        } else {
            parent.insertAfter(item, this);
        }
        return item.getLast(drawer);
    }

    public Cursor removeChar(Drawer drawer, int pos) {
        if (parent == null) {
            return null;
        }
        if (pos == 0) {
            parent.removeLeft(drawer, this);
            return getFirst(drawer);
        } else {
            parent.removeRight(drawer, this);
            return getLast(drawer);
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

    public Command deleteLeft(final Drawer drawer, final Cursor cursor) {
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem THIS = this;

        if (cursor.getPosition() <= 0) { //Remove item to the left
            final FormulaItem left = parent.getLeftItem(drawer, THIS);
            if(left == null) return Command.ZERO_COMMAND;

            return new Command() {

                public Cursor execute() {
                    return parent.removeLeft(drawer, THIS);
                }

                public void undo() {
                    parent.insertBefore(left, THIS);
                }
            };
        } else { //Remove this item
            return new Command() {
                final int pos = parent.getItemPosition(THIS);

                public Cursor execute() {
                    Cursor newCursor = parent.getLeft(drawer, THIS);
                    parent.remove(THIS);

                    if (newCursor == null) {
                        newCursor = parent.getFirst(drawer);
                    }

                    return newCursor;
                }

                public void undo() {
                    parent.insertAt(pos, THIS);
                }
            };
        }
    }


    public Command deleteRight(final Drawer drawer, final Cursor cursor) {
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final FormulaItem THIS = this;

        if (cursor.getPosition() > 0) { //Remove item to the right
            final FormulaItem right = parent.getRightItem(drawer, THIS);
            if(right == null) return Command.ZERO_COMMAND;

            return new Command() {

                public Cursor execute() {
                    return parent.removeRight(drawer, THIS);
                }

                public void undo() {
                    parent.insertAfter(right, THIS);
                }
            };
        } else { //Remove this item
            return new Command() {
                final int pos = parent.getItemPosition(THIS);

                public Cursor execute() {
                    Cursor newCursor = parent.getRight(drawer, THIS);
                    parent.remove(THIS);

                    if (newCursor == null) {
                        newCursor = parent.getLast(drawer);
                    }

                    return newCursor;
                }

                public void undo() {
                    parent.insertAt(pos, THIS);
                }
            };
        }
    }

}
