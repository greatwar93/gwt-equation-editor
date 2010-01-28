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

    protected boolean addNext = false; //Should next item be concatenated with this in the case of undo.

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

    public boolean isAddNext() {
        return addNext;
    }

    public void setAddNext(boolean addNext) {
        this.addNext = addNext;
    }

    public Cursor breakWith(int pos, FormulaItem item) {
        addNext = false;
        if (parent == null) {
            return item.getCursor(pos);
        }

        if (pos < val.length()) {
            addNext = true;
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

    public void addItem(SimpleElement item){
        val = val + item.getName();
        setPower(item.getPower());
        addNext = false;
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

        if (val.length() <= 0) {
            if (parent != null) {
                final Formula parent_backup = parent;
                Cursor cursor = parent_backup.getLeft(this);

                parent_backup.remove(this);

                if (cursor.getItem() == this) {
                    return parent_backup.getFirst();
                } else {
                    return cursor;
                }
            }
        }

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
        final Formula parent_backup = parent;
        if (pos <= 0) { //Delete adjecent item
            if (parent_backup == null) {
                return Command.ZERO_COMMAND;
            }

            final FormulaItem left = parent_backup.getLeftItem(this);
            return new Command() {

                public Cursor execute() {
                    return parent_backup.removeLeft(THIS);
                }

                public void undo() {
                    parent_backup.insertBefore(left, THIS);
                }
            };


        } else if (val.length() <= 1) { //Delete this item, cause it is empty now
            if (parent_backup == null) {
                return Command.ZERO_COMMAND;
            }

            final FormulaItem left = parent_backup.getLeftItem(this);
            if (left != null) {
                return new Command() {

                    public Cursor execute() {
                        parent_backup.remove(THIS);
                        return left.getLast();
                    }

                    public void undo() {
                        parent_backup.insertAfter(THIS, left);
                    }
                };
            }

            final FormulaItem right = parent_backup.getRightItem(this);
            if (right != null) {
                return new Command() {

                    public Cursor execute() {
                        parent_backup.remove(THIS);
                        return right.getFirst();
                    }

                    public void undo() {
                        parent_backup.insertBefore(THIS, right);
                    }
                };
            }

            return new Command() {

                public Cursor execute() {
                    parent_backup.remove(THIS);
                    return parent_backup.getFirst();
                }

                public void undo() {
                    parent_backup.add(THIS);
                }
            };
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
        final Formula parent_backup = parent;
        if (pos >= val.length()) { //Delete adjacent item
            if (parent_backup == null) {
                return Command.ZERO_COMMAND;
            }

            final FormulaItem right = parent_backup.getRightItem(this);
            return new Command() {

                public Cursor execute() {
                    return parent_backup.removeRight(THIS);
                }

                public void undo() {
                    parent_backup.insertAfter(right, THIS);
                }
            };
        } else if (val.length() <= 1) { //Delete this item, cause it is empty now
            if (parent_backup == null) {
                return Command.ZERO_COMMAND;
            }

            final FormulaItem right = parent_backup.getRightItem(this);
            if (right != null) {
                return new Command() {

                    public Cursor execute() {
                        parent_backup.remove(THIS);
                        return right.getFirst();
                    }

                    public void undo() {
                        parent_backup.insertBefore(THIS, right);
                    }
                };
            }

            final FormulaItem left = parent_backup.getLeftItem(this);
            if (left != null) {
                return new Command() {

                    public Cursor execute() {
                        parent_backup.remove(THIS);
                        return left.getLast();
                    }

                    public void undo() {
                        parent_backup.insertAfter(THIS, left);
                    }
                };
            }

            return new Command() {

                public Cursor execute() {
                    parent_backup.remove(THIS);
                    return parent_backup.getFirst();
                }

                public void undo() {
                    parent_backup.add(THIS);
                }
            };
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
