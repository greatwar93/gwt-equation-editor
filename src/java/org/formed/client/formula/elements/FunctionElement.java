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
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.drawer.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class FunctionElement extends PoweredElement {

    //private String name;
    private Formula formula;
    //private final RightCloser right = new RightCloser();
    //private final LeftCloser left = new LeftCloser();

    public FunctionElement(String name) {
        super();
        setName(name);
        setFormula(new Formula(true));
    }

    public FunctionElement(String name, Formula formula) {
        super();
        setName(name);
        setFormula(formula);
        formula.setShowPlace(true);
    }

    public FunctionElement(String name, Formula formula, Formula power) {
        super(power);
        setName(name);
        setFormula(formula);
        formula.setShowPlace(true);
    }

    public FormulaItem makeClone() {
        FunctionElement clone = new FunctionElement(val, formula.makeClone(), getPower().makeClone());
//        FunctionElement clone = new FunctionElement(name, formula.makeClone(), getPower().makeClone());
        clone.setParent(parent);

        return clone;
    }

    public String getName() {
        return val;
        //return name;
    }

    public void setName(String name) {
        //this.name = name;
        val = name;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        if (formula == null) {
            this.formula = new Formula(true, true);
        } else {
            this.formula = formula;
            formula.setShowPlace(true);
            formula.setAutoBrackets(true);
        }
        formula.setParent(this);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && formula.isEmpty();
    }

    @Override
    public boolean isYouOrInsideYou(FormulaItem item) {
        if (super.isYouOrInsideYou(item)) {
            return true;
        }
        if (formula.isInsideYou(item)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLastPosition(int position) {
        return position == -1;
    }

    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
        Metrics metrics = measure(drawer, size);

        if (highlighted) {
            drawer.fillRect(x, y - metrics.getHeightUp(), x + metrics.getWidth(), y + metrics.getHeightDown(), highlightR, highlightG, highlightB);
        }

        metrics = super.draw(drawer, x + 2, y, size);
        metrics.setWidth(metrics.getWidth() + 4);

        if (formula != null) {
            /*            if (formula.isComplex()) {
            metrics.add(left.draw(drawer, x + metrics.getWidth(), y, size));
            metrics.add(formula.draw(drawer, x + metrics.getWidth(), y, size));
            metrics.add(right.draw(drawer, x + metrics.getWidth(), y, size));
            } else {*/
            metrics.add(formula.draw(drawer, x + metrics.getWidth(), y, size));
//            }
        }

        metrics.setWidth(metrics.getWidth() + 2);
        if (strokeThrough) {
            drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
        }

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        Metrics metrics = super.measure(drawer, size);
        metrics.setWidth(metrics.getWidth() + 6);

        if (formula != null) {
            /*            if (formula.isComplex()) {
            metrics.add(left.measure(drawer, size));
            metrics.add(formula.calculateMetrics(drawer, size));
            metrics.add(right.measure(drawer, size));
            } else {*/
            metrics.add(formula.calculateMetrics(drawer, size));
//            }
        }

        return metrics;
    }

    @Override
    public Cursor getLast() {
        return getCursor(-1);
    }

    @Override
    public Cursor getEditPlace() {
        return formula.getLast();
    }

    @Override
    public Cursor childAsksLeft(Formula child) {
        if (child == formula) {
            return super.getLast();
        }

        return super.childAsksLeft(child);
    }

    @Override
    public Cursor childAsksRight(Formula child) {
        if (child == getPower()) {
            return formula.getFirst();
        }

        if (child == formula) {
            return getCursor(-1);
        }

        return super.childAsksRight(child);
    }

    @Override
    public Cursor childAsksDown(Formula child) {
        if (child == getPower()) {
            return super.getLast();
        }
        return super.childAsksDown(child);
    }

    @Override
    public Cursor getLeft(int oldPosition) {
        if (oldPosition == -1) {
            return formula.getLast();
        }

        int position = oldPosition - 1;

        if (position < 0) {
            if (parent == null) {
                return null;
            }

            return parent.getLeft(this);
        }

        return getCursor(position);
    }

    @Override
    public Cursor getRight(int oldPosition) {
        if (oldPosition == -1) {
            return parent.getRight(this);
        }

        if (oldPosition >= val.length()) {
            if (formula != null) {
                return formula.getFirst();
            }
        }

        return super.getRight(oldPosition);
    }

    @Override
    public Cursor getCursor(int position) {
        if (position == -1) {
            return new Cursor(this, -1);
        }
        return super.getCursor(position);
    }

    @Override
    public void measureCursor(Drawer drawer, Cursor cursor) {
        if (cursor.getPosition() == -1) {
            Metrics metrics = measure(drawer, storedSize);
            cursor.setX(storedX + metrics.getWidth());
            cursor.setY(storedY);
            cursor.setHeightUp(metrics.getHeightUp());
            cursor.setHeightDown(metrics.getHeightDown());
        } else {
            super.measureCursor(drawer, cursor);
        }
    }

    @Override
    public void invalidatePlaces(Formula source) {
        super.invalidatePlaces(source);
        if (formula != source) {
            formula.invalidatePlaces(this);
        }
    }

    @Override
    public void invalidateMetrics() {
        super.invalidateMetrics();
        formula.invalidateMetrics();
    }

    @Override
    public Command buildDeleteLeft(Cursor cursor, final CursorFixer fixer) {
        //position -1 is a rightmost position for a function, that differs from other FormulaItems, so this method should be handled here
        if (parent == null) {
            return Command.ZERO_COMMAND;
        }

        final Formula parent_backup = parent;

        if (cursor.getPosition() == 0) { //Remove item to the left
            FormulaItem left = parent_backup.getLeftItem(this);
            if (left == null || left == this) {
                return Command.ZERO_COMMAND;
            }

            return left.buildDeleteLeft(left.getLast(), fixer);
        } else if (cursor.getPosition() == -1) { //Remove this item
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
        } else {
            return super.buildDeleteLeft(cursor, fixer);
        }
    }

    @Override
    public HowToInsert getHowToInsert(Cursor cursor, FormulaItem item) {
        if (item == null || cursor == null) {
            return HowToInsert.NONE;
        }

        if (cursor.getPosition() == -1) {
            return HowToInsert.RIGHT;
        }

        if (item instanceof RootElement || item instanceof FunctionElement) {
            return cursor.getPosition() == -1 ? HowToInsert.RIGHT : HowToInsert.LEFT;
        } else if (!(item instanceof SimpleElement)) {
            return cursor.getPosition() == 0 ? HowToInsert.LEFT : HowToInsert.RIGHT;
        }

        SimpleElement simpleItem = (SimpleElement) item;
        return simpleItem.getPower().isEmpty() ? HowToInsert.INSERT : HowToInsert.BREAK;
    }

    @Override
    public Command buildIncorporateRight(final CursorFixer fixer) {
        return buildIncorporateRight(formula, fixer);
    }
}
