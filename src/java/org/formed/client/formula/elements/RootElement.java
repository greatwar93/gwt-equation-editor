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
public final class RootElement extends BaseElement {

    private Formula formula;
    private Formula formulaPower;

    public RootElement(Formula formula) {
        setFormula(formula);
        formula.setShowPlace(true);
        setFormulaPower(new Formula());
    }

    public RootElement(Formula formula, Formula formulaPower) {
        setFormula(formula);
        formula.setShowPlace(true);
        setFormulaPower(formulaPower);
    }

    public FormulaItem makeClone() {
        RootElement clone = new RootElement(formula.makeClone(), formulaPower.makeClone());
        clone.setParent(parent);

        return clone;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        if (formula == null) {
            this.formula = new Formula();
        } else {
            this.formula = formula;
            formula.setShowPlace(true);
        }
        formula.setParent(this);
    }

    public Formula getFormulaPower() {
        return formulaPower;
    }

    public void setFormulaPower(Formula formulaPower) {
        if (formulaPower == null) {
            this.formulaPower = new Formula();
        } else {
            this.formulaPower = formulaPower;
        }
        formulaPower.setParent(this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isYouOrInsideYou(FormulaItem item) {
        if (super.isYouOrInsideYou(item)) {
            return true;
        }
        if (formula.isInsideYou(item)) {
            return true;
        }
        if (formulaPower.isInsideYou(item)) {
            return true;
        }
        return false;
    }

    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        Metrics metrics = measure(drawer, size);
        if (highlighted) {
            drawer.fillRect(x, y - metrics.getHeightUp(), x + metrics.getWidth(), y + metrics.getHeightDown(), highlightR, highlightG, highlightB);
        }

        metrics = new Metrics(4, 0, 0);
        Metrics metrics2 = formula.calculateMetrics(drawer, size);

        if (formulaPower != null) {
            int powerSize = drawer.getSmallerSize(size);
            metrics = formulaPower.calculateMetrics(drawer, powerSize);
            formulaPower.draw(drawer, x, y - metrics.getHeightDown() - 2, powerSize);
            if (metrics.getWidth() < 4) {
                metrics.setWidth(4);
            }
        }

        formula.draw(drawer, x + metrics.getWidth() + 4, y, size);

        drawer.drawLine(x, y, x + metrics.getWidth(), y);
        drawer.drawLine(x + metrics.getWidth(), y, x + metrics.getWidth() + 2, y + metrics2.getHeightDown() - 1);
        drawer.drawLine(x + metrics.getWidth() + 2, y + metrics2.getHeightDown() - 1, x + metrics.getWidth() + 4, y - metrics2.getHeightUp() - 1);
        drawer.drawLine(x + metrics.getWidth() + 4, y - metrics2.getHeightUp() - 1, x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() - 1);
        drawer.drawLine(x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() - 1, x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() + 2);
        /*        drawer.getCanvas().beginPath();
        drawer.getCanvas().moveTo(x, y);
        drawer.getCanvas().lineTo(x + metrics.getWidth(), y);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 2, y + metrics2.getHeightDown() - 1);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 4, y - metrics2.getHeightUp() - 1);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() - 1);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() + 2);
        drawer.getCanvas().stroke();*/

        metrics.setWidth(metrics.getWidth() + metrics2.getWidth() + 6);
        metrics.setHeightUp(Math.max(metrics2.getHeightUp() + 4, metrics.getHeight() + metrics2.getHeight() / 2));
        metrics.setHeightDown(metrics2.getHeightDown());

        if (strokeThrough) {
            drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
        }

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        storedSize = size;

        Metrics metrics = new Metrics(4, 0, 0);

        if (formulaPower != null) {
            int powerSize = drawer.getSmallerSize(size);
            metrics = formulaPower.calculateMetrics(drawer, powerSize);
            if (metrics.getWidth() < 4) {
                metrics.setWidth(4);
            }
        }

        Metrics metrics2 = formula.calculateMetrics(drawer, size);

        metrics.setWidth(metrics.getWidth() + metrics2.getWidth() + 6);
        metrics.setHeightUp(Math.max(metrics2.getHeightUp() + 4, metrics.getHeight() + metrics.getHeightDown() + 2));
        metrics.setHeightDown(metrics2.getHeightDown());

        return metrics;
    }

    @Override
    public void measureCursor(Drawer drawer, Cursor cursor) {
        Metrics metrics = measure(drawer, storedSize);
        if (cursor.getPosition() == 0) {
            cursor.setCursor(new Cursor(this, 0, storedX, storedY, metrics.getHeightUp(), metrics.getHeightDown()));
        } else {
            cursor.setCursor(new Cursor(this, 1, storedX + metrics.getWidth(), storedY, metrics.getHeightUp(), metrics.getHeightDown()));
        }
    }

    @Override
    public Cursor getCursor(Drawer drawer, int x, int y) {
        Metrics metrics = measure(drawer, storedSize);
        if (x - storedX < metrics.getWidth() / 2) {
            return new Cursor(this, 0, storedX, storedY, metrics.getHeightUp(), metrics.getHeightDown());
        } else {
            return new Cursor(this, 1, storedX + metrics.getWidth(), storedY, metrics.getHeightUp(), metrics.getHeightDown());
        }
    }

    @Override
    public boolean isLastPosition(int position) {
        return position == 1;
    }

    @Override
    public Cursor getCursor(int position) {
        if (position == 0) {
            return new Cursor(this, 0);
        } else {
            return new Cursor(this, 1);
        }
    }

    @Override
    public Cursor getMovementFirst() {
        return getCursor(0);
    }

    @Override
    public Cursor getLast() {
        //return formula.getLast();
        return getCursor(1);
    }

    @Override
    public Cursor getEditPlace() {
        return formula.getLast();
    }

    @Override
    public Cursor getLeft(int oldPosition) {
        if (oldPosition == 1) {
            return formula.getLast();
        }

        return parent.getLeft(this);
    }

    @Override
    public Cursor getRight(int oldPosition) {
        if (oldPosition == 0) {
            return formulaPower.getFirst();
        }

        return parent.getRight(this);
    }

    @Override
    public Cursor childAsksLeft(Formula child) {
        if (child == formula) {
            return formulaPower.getLast();
        }

        return getMovementFirst();
    }

    @Override
    public Cursor childAsksRight(Formula child) {
        if (child == formulaPower) {
            return formula.getFirst();
        }

        return getCursor(1);
        //return getLast();
    }

    @Override
    public Cursor childAsksUp(Formula child) {
        if (child == formula) {
            return formulaPower.getLast();
        }
        return super.childAsksUp(child);
    }

    @Override
    public Cursor childAsksDown(Formula child) {
        if (child == formulaPower) {
            return formula.getFirst();
        }
        return super.childAsksDown(child);
    }

    @Override
    public void invalidatePlaces(Formula source) {
        super.invalidatePlaces(source);
        if (formula != source) {
            formula.invalidatePlaces(this);
        }
        if (formulaPower != source) {
            formulaPower.invalidatePlaces(this);
        }
    }

    @Override
    public void invalidateMetrics() {
        super.invalidateMetrics();
        formula.invalidateMetrics();
        formulaPower.invalidateMetrics();
    }

    @Override
    public Command buildDeleteLeft(Cursor cursor, CursorFixer fixer) {
        return buildSimpleDeleteLeft(cursor, fixer);
    }

    @Override
    public Command buildDeleteRight(Cursor cursor, CursorFixer fixer) {
        return buildSimpleDeleteRight(cursor, fixer);
    }

    @Override
    public HowToInsert getHowToInsert(Cursor cursor, FormulaItem item) {
        if (item == null || cursor == null) {
            return HowToInsert.NONE;
        }

        return cursor.getPosition() <= 0 ? HowToInsert.LEFT : HowToInsert.RIGHT;
    }

    @Override
    public Command buildIncorporateRight(final CursorFixer fixer) {
        return buildIncorporateRight(formula, fixer);
    }
}
