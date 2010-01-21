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

import org.formed.client.formula.Cursor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.Drawer;
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

    public Cursor breakWith(Drawer drawer, int pos, FormulaItem item) {
        if (parent == null) {
            return item.getCursor(drawer, pos);
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

        return item.getLast(drawer); //Why doesn't it work ?
    }

    public String getTextBefore(Cursor cursor) {
        return val.substring(0, cursor.getPosition());
    }

    public String getTextAfter(Cursor cursor) {
        return val.substring(cursor.getPosition());
    }

    @Override
    public Cursor insertChar(Drawer drawer, Cursor cursor, char c) {
        return insertChar(drawer, cursor.getPosition(), c);
    }

    @Override
    public Cursor insertChar(Drawer drawer, int pos, char c) {
        val = val.substring(0, pos) + c + val.substring(pos);
        invalidatePlaces(null);
        return getCursor(drawer, pos + 1);
    }

    @Override
    public Cursor removeChar(Drawer drawer, int pos){
        val = val.substring(0, pos) + val.substring(pos+1);
        invalidatePlaces(null);
        return getCursor(drawer, pos);
    }

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
}
