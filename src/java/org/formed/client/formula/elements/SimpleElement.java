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

import org.formed.client.formula.editor.Command;
import org.formed.client.formula.editor.Cursor;
import org.formed.client.formula.editor.CursorFixer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.FormulaItem.HowToInsert;

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

    public SimpleElement(String name, boolean strokeThrough) {
        super(strokeThrough);
        setName(name);
    }

    public SimpleElement(String name, Formula power, boolean strokeThrough) {
        super(power, strokeThrough);
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

    protected void addItem(SimpleElement item) {
        val = val + item.getName();
        setPower(item.getPower());
        item.setPower(null);
        //item.setName("");
    }

    protected SimpleElement breakWith(int pos, FormulaItem item, SimpleElement newItem) {
        if (parent == null) {
            return null;
        }

        if (pos < val.length()) {
            parent.insertAfter(item, this);
            if (newItem == null) {
                newItem = new SimpleElement(val.substring(pos), getPower());
            }else{
                newItem.setName(val.substring(pos));
                newItem.setPower(getPower());
            }
            parent.insertAfter(newItem, item);
            val = val.substring(0, pos);
            setPower(null);
        } else {
            parent.insertAfter(item, this);
        }

        invalidatePlaces(null);

        return newItem;
    }

    @Override
    public Command buildBreakWith(final Cursor cursor, final FormulaItem item, final CursorFixer fixer) {
        if (!hasParent() || item == null || cursor == null) {
            return Command.ZERO_COMMAND;
        }
        final Formula parent_backup = parent;
        final int pos = cursor.getPosition();

        return new Command() {

            SimpleElement newItem = null;

            public Cursor execute() {
                newItem = breakWith(pos, item, newItem);
                return item.getEditPlace();
            }

            public void undo() {
                parent_backup.remove(item);
                fixer.removed(item, getCursor(pos));
                if (newItem == null) {
                    return;
                }

                addItem(newItem);
                parent_backup.remove(newItem);
                fixer.removed(newItem, getCursor(pos));
            }
        };
    }

    @Override
    public HowToInsert getHowToInsert(Cursor cursor, FormulaItem item) {
        if (item == null || cursor == null) {
            return HowToInsert.NONE;
        }

        if (item instanceof SimpleElement) {
            if (((SimpleElement) item).getPower().isEmpty()) {
                return HowToInsert.INSERT;
            }
        }

        int pos = cursor.getPosition();
        if (pos <= 0) {
            return HowToInsert.LEFT;
        } else if (pos >= val.length()) {
            return HowToInsert.RIGHT;
        } else {
            return HowToInsert.BREAK;
        }
    }

    @Override
    public Command buildIncorporateLeft(final CursorFixer fixer) {
        if (!hasParent()) {
            return Command.ZERO_COMMAND;
        }
        FormulaItem item = parent.getLeftItem(this);
        if (item == null) {
            return Command.ZERO_COMMAND;
        }
        if (!(item instanceof SimpleElement)) {
            return Command.ZERO_COMMAND;
        }
        final SimpleElement simpleItem = (SimpleElement) item;
        if (!simpleItem.getPower().isEmpty()) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;
        return new Command() {

            public Cursor execute() {
                parent_backup.remove(simpleItem);
                Cursor newCursor = insertString(0, simpleItem.getName());
                fixer.removed(simpleItem, newCursor);
                return newCursor;
            }

            public void undo() {
                undoInsertString(0, simpleItem.getName(), fixer);
                parent_backup.insertBefore(simpleItem, THIS);
            }
        };
    }

    @Override
    public Command buildIncorporateRight(final CursorFixer fixer) {
        if (!hasParent() || !getPower().isEmpty()) {
            return Command.ZERO_COMMAND;
        }
        FormulaItem item = parent.getRightItem(this);
        if (item == null) {
            return Command.ZERO_COMMAND;
        }
        if (!(item instanceof SimpleElement)) {
            return Command.ZERO_COMMAND;
        }
        final SimpleElement simpleItem = (SimpleElement) item;
        final int pos = val.length();
        final Formula parent_backup = parent;
        return new Command() {

            public Cursor execute() {
                setPower(simpleItem.getPower());
                simpleItem.setPower(null);
                parent_backup.remove(simpleItem);
                insertString(pos, simpleItem.getName());
                Cursor newCursor = getCursor(pos);
                fixer.removed(simpleItem, newCursor);
                return newCursor;
            }

            public void undo() {
                simpleItem.setPower(getPower());
                setPower(null);
                undoInsertString(pos, simpleItem.getName(), fixer);
                parent_backup.insertAfter(simpleItem, THIS);
            }
        };
    }
}
