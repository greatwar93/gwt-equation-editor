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
package org.formed.client.formula.editor;

import org.formed.client.formula.*;

/**
 * Object used as a cursor. It points to a specific position in a specific element.
 * And supports cursor movements and measurement.
 * @author Bulat Sirazetdinov
 */
public final class Cursor {

    private FormulaItem item;
    private int position;
    private int x = 0;
    private int y = 0;
    private int heightUp = 0;
    private int heightDown = 0;

    /**
     * Most common constructor (does not set cursor metrics)
     * @param item element to place cursor at
     * @param position position in a specified element to place cursor at
     */
    public Cursor(FormulaItem item, int position) {
        this.item = item;
        this.position = position;
    }

    /**
     * Constructor that sets all parameters of a cursor (including metrics)
     * @param item element to place cursor at
     * @param position position in a specified element to place cursor at
     * @param x x coordinate of a cursor baseline beginning
     * @param y y coordinate of a cursor baseline
     * @param heightUp cursor height above baseline
     * @param heightDown cursor height below baseline
     */
    public Cursor(FormulaItem item, int position, int x, int y, int heightUp, int heightDown) {
        this.item = item;
        this.position = position;
        this.x = x;
        this.y = y;
        this.heightUp = heightUp;
        this.heightDown = heightDown;
    }

    /**
     * Create a clone of a cursor
     * @return newly created clone of a cursor
     */
    public Cursor makeClone() {
        return new Cursor(item, position, x, y, heightUp, heightDown);
    }

    /**
     * Set all cursor parameters to the values from specified cursor
     * @param cursor cursor to copy values from
     */
    public void setCursor(Cursor cursor) {
        if (cursor == null) {
/*            item = null;
            position = 0;
            x = 0;
            y = 0;
            heightUp = 0;
            heightDown = 0;*/
        } else {
            item = cursor.item;
            position = cursor.position;
            x = cursor.x;
            y = cursor.y;
            heightUp = cursor.heightUp;
            heightDown = cursor.heightDown;
        }
    }

    /**
     * Returns cursor height below baseline
     * @return cursor height below baseline
     */
    public int getHeightDown() {
        return heightDown;
    }

    /**
     * Returns cursor height above baseline
     * @return cursor height above baseline
     */
    public int getHeightUp() {
        return heightUp;
    }

    /**
     * Set cursor height below baseline to the specified value
     * @param heightDown cursor height below baseline
     */
    public void setHeightDown(int heightDown) {
        this.heightDown = heightDown;
    }

    /**
     * Set cursor height above baseline to the specified value
     * @param heightUp cursor height above baseline
     */
    public void setHeightUp(int heightUp) {
        this.heightUp = heightUp;
    }

    /**
     * Returns x coordinate of a cursor baseline beginning
     * @return x coordinate of a cursor baseline beginning
     */
    public int getX() {
        return x;
    }

    /**
     * Returns y coordinate of a cursor baseline
     * @return y coordinate of a cursor baseline
     */
    public int getY() {
        return y;
    }

    /**
     * Set x coordinate of a cursor baseline beginning to the specified value
     * @param x x coordinate of a cursor baseline beginning
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Set y coordinate of a cursor baseline to the specified value
     * @param y y coordinate of a cursor baseline
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns cursor position in an element
     * @return cursor position in an element
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set cursor position in an element to the specified value
     * @param position cursor position in an element
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Return element at which cursor is positioned
     * @return element at which cursor is positioned
     */
    public FormulaItem getItem() {
        return item;
    }

    /**
     * Move cursor right
     * @return this cursor
     */
    public Cursor moveRight() {
        if (item == null) {
            return this;
        }
        setCursor(item.getRight(position));
        return this;
    }

    /**
     * Move cursor left
     * @return this cursor
     */
    public Cursor moveLeft() {
        if (item == null) {
            return this;
        }
        setCursor(item.getLeft(position));
        return this;
    }

    /**
     * Move cursor up
     * @return this cursor
     */
    public Cursor moveUp() {
        if (item == null) {
            return this;
        }
        setCursor(item.getUp(position));
        return this;
    }

    /**
     * Move cursor down
     * @return this cursor
     */
    public Cursor moveDown() {
        if (item == null) {
            return this;
        }
        setCursor(item.getDown(position));
        return this;
    }

    /**
     * Move cursor to the first position of a first element in a formula.
     * If it is already in that position,
     * then move to the first position of a first element of a parent formula.
     * @return this cursor
     */
    public Cursor moveFirst() {
        if (item == null) {
            return this;
        }
        Formula cursorFormula = item.getParent();
        if (cursorFormula == null) {
            return this;
        }

        if (cursorFormula.isFirst(item) && item.isFirstPosition(position)) {
            FormulaItem parentItem = cursorFormula.getParent();
            if (parentItem == null) {
                return this;
            }

            Formula parentFormula = parentItem.getParent();
            if (parentFormula != null) {
                setCursor(parentFormula.getFirst());
            }
        } else {
            setCursor(cursorFormula.getFirst());
        }

        return this;
    }

    /**
     * Move cursor to the last position of a last element in a formula.
     * If it is already in that position,
     * then move to the last position of a last element of a parent formula.
     * @return this cursor
     */
    public Cursor moveLast() {
        if (item == null) {
            return this;
        }
        Formula cursorFormula = item.getParent();
        if (cursorFormula == null) {
            return this;
        }

        if (cursorFormula.isLast(item) && item.isLastPosition(position)) {
            FormulaItem parentItem = cursorFormula.getParent();
            if (parentItem == null) {
                return this;
            }

            Formula parentFormula = parentItem.getParent();
            if (parentFormula != null) {
                setCursor(parentFormula.getLast());
            }
        } else {
            setCursor(cursorFormula.getLast());
        }

        return this;
    }

    /**
     * Calculate cursor metrics (x, y, heightUp, heightDown).
     * x - x coordinate of a cursor baseline beginning
     * y - y coordinate of a cursor baseline
     * heightUp - cursor height above baseline
     * heightDown - cursor height below baseline
     * They can be measured only after element that cursor points to being drawn.
     * @param drawer Drawer object to use for metrics calculation
     */
    public void measure(Drawer drawer) {
        if (item != null) {
            item.measureCursor(drawer, this);
        }
    }
}
