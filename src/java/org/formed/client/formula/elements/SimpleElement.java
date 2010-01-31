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
import org.formed.client.formula.FormulaItem.HowToInsert;

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

    public void addItem(SimpleElement item) {
        val = val + item.getName();
        setPower(item.getPower());
        item.setPower(null);
        addNext = false;
    }

    protected SimpleElement breakWith1(int pos, FormulaItem item) {
        addNext = false;
        if (parent == null) {
            return null;
        }

        SimpleElement newItem = null;
        if (pos < val.length()) {
            addNext = true;
            parent.insertAfter(item, this);
            newItem = new SimpleElement(val.substring(pos), getPower());
            parent.insertAfter(newItem, item);
            val = val.substring(0, pos);
            setPower(null);
        } else {
            parent.insertAfter(item, this);
        }

        invalidatePlaces(null);

        return newItem;
    }

    public Cursor breakWith(int pos, FormulaItem item) {
        addNext = false;
        if (parent == null) {
            return item.getCursor(pos);
        }

        breakWith1(pos, item);
        /*
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
         */
        return item.getLast(); //Why doesn't it work ?
    }

    @Override
    public Command buildDeleteLeft(Cursor cursor) {
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
    public Command buildDeleteRight(Cursor cursor) {
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

    @Override
    public Command buildBreakWith(final Cursor cursor, final FormulaItem item) {
        if (!hasParent() || item == null || cursor == null) {
            return Command.ZERO_COMMAND;
        }
        final Formula parent_backup = parent;
        final int pos = cursor.getPosition();

        return new Command() {

            public Cursor execute() {
                breakWith(pos, item);
                return item.getEditPlace();
            }

            public void undo() {
                parent_backup.remove(item);
                if (!isAddNext()) {
                    return;
                }

                setAddNext(false);
                FormulaItem item = parent_backup.getRightItem(THIS);
                if (!(item instanceof SimpleElement)) {
                    return;
                }

                addItem((SimpleElement) item);
                parent_backup.remove(item);
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
    public Command buildIncorporateLeft() {
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
                simpleItem.removeFromParent();
                return insertString(0, simpleItem.getName());
            }

            public void undo() {
                undoInsertString(0, simpleItem.getName());
                parent_backup.insertBefore(simpleItem, THIS);
            }
        };
    }

    @Override
    public Command buildIncorporateRight() {
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
                simpleItem.removeFromParent();
                return insertString(pos, simpleItem.getName());
            }

            public void undo() {
                simpleItem.setPower(getPower());
                setPower(null);
                undoInsertString(pos, simpleItem.getName());
                parent_backup.insertAfter(simpleItem, THIS);
            }
        };
    }
}
