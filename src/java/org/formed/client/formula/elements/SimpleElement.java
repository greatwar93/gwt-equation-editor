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
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class SimpleElement extends PoweredElement {

    public SimpleElement(String name) {
        super();
        setName(name);
    }

    public SimpleElement(String name, Formula power) {
        super(power);
        setName(name);
    }

    public FormulaItem makeClone() {
        SimpleElement clone = new SimpleElement(val, getPower().makeClone());
        clone.setParent(parent);

        return clone;
    }

    public String getName() {
        return val;
    }

    public void setName(String name) {
        val = name;
    }

    public Cursor breakWith(int pos, FormulaItem item) {
        if (parent == null) {
            return item.getCursor(pos);
        }

        if (pos < val.length()) {
            parent.insertAfter(item, this);
            parent.insertAfter(new SimpleElement(val.substring(pos), getPower()), item);
            setName(val.substring(0, pos));
            setPower(null);
        } else {
            parent.insertAfter(item, this);
        }

        invalidatePlaces(null);

        return item.getLast(); //Why doesn't it work ?
    }

    public String getTextBefore(Cursor cursor) {
        return val.substring(0, cursor.getPosition());
    }

    public String getTextAfter(Cursor cursor) {
        return val.substring(cursor.getPosition());
    }

    @Override
    public Cursor insertChar(Cursor cursor, char c) {
        return insertChar(cursor.getPosition(), c);
    }

    @Override
    public Cursor insertChar(int pos, char c) {
        val = val.substring(0, pos) + c + val.substring(pos);
        invalidatePlaces(null);
        return getCursor(pos + 1);
    }

    @Override
    public Cursor removeChar(int pos) {
        val = val.substring(0, pos) + val.substring(pos + 1);
        invalidatePlaces(null);
        return getCursor(pos);
    }
    /*
    @Override
    public Cursor deleteLeft(Drawer drawer, Cursor cursor) {
    int pos = cursor.getPosition();
    if (pos <= 0) {
    if (parent != null) {
    return parent.removeLeft(drawer, this);
    }
    return cursor;
    }

    val = val.substring(0, pos - 1) + val.substring(pos);
    cursor.setPosition(pos - 1);

    return cursor;
    }

    @Override
    public Cursor deleteRight(Drawer drawer, Cursor cursor) {
    int pos = cursor.getPosition();
    if (pos >= val.length()) {
    if (parent != null) {
    return parent.removeRight(drawer, this);
    }
    return cursor;
    }

    val = val.substring(0, pos) + val.substring(pos + 1);

    return cursor;
    }
     */

    @Override
    public Command deleteLeft(Cursor cursor) {
        final FormulaItem THIS = this;
        final int pos = cursor.getPosition();
        final Cursor newCursor = cursor.makeClone();
        if (pos <= 0) { //Delete adjecent item
            if (parent != null) {
                final FormulaItem left = parent.getLeftItem(this);
                return new Command() {

                    public Cursor execute() {
                        return parent.removeLeft(THIS);
                    }

                    public void undo() {
                        parent.insertBefore(left, THIS);
                    }
                };
            }
            return Command.ZERO_COMMAND;
        }

        //Delete char
        final String oldVal = val;
        return new Command() {

            public Cursor execute() {
                val = val.substring(0, pos - 1) + val.substring(pos);
                newCursor.setPosition(pos - 1);

                return newCursor;
            }

            public void undo() {
                val = oldVal;
            }
        };
    }

    @Override
    public Command deleteRight(Cursor cursor) {
        final FormulaItem THIS = this;
        final int pos = cursor.getPosition();
        final Cursor newCursor = cursor.makeClone();
        if (pos >= val.length()) { //Delete adjacent item
            if (parent != null) {
                final FormulaItem right = parent.getRightItem(this);
                return new Command() {

                    public Cursor execute() {
                        return parent.removeRight(THIS);
                    }

                    public void undo() {
                        parent.insertAfter(right, THIS);
                    }
                };
            }
            return Command.ZERO_COMMAND;
        }

        //Delete char
        final String oldVal = val;
        return new Command() {

            public Cursor execute() {
                val = val.substring(0, pos) + val.substring(pos + 1);

                return newCursor;
            }

            public void undo() {
                val = oldVal;
            }
        };
    }
}
