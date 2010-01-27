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
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class DivisorElement extends BaseElement {

    private Formula formula1;
    private Formula formula2;

    public DivisorElement() {
        setFormula1(new Formula(true));
        setFormula2(new Formula(true));
    }

    public DivisorElement(Formula formula1, Formula formula2) {
        setFormula1(formula1);
        formula1.setShowPlace(true);
        setFormula2(formula2);
        formula2.setShowPlace(true);
    }

    public FormulaItem makeClone() {
        DivisorElement clone = new DivisorElement(formula1.makeClone(), formula2.makeClone());
        clone.setParent(parent);

        return clone;
    }

    public Formula getFormula1() {
        return formula1;
    }

    public void setFormula1(Formula formula1) {
        if (formula1 == null) {
            this.formula1 = new Formula(true);
        } else {
            this.formula1 = formula1;
            formula1.setShowPlace(true);
        }
        formula1.setParent(this);
    }

    public Formula getFormula2() {
        return formula2;
    }

    public void setFormula2(Formula formula2) {
        if (formula2 == null) {
            this.formula2 = new Formula(true);
        } else {
            this.formula2 = formula2;
            formula1.setShowPlace(true);
        }
        formula2.setParent(this);
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        Metrics metrics = formula1.calculateMetrics(drawer, size);
        Metrics metrics2 = formula2.calculateMetrics(drawer, size);

        int width = Math.max(metrics.getWidth(), metrics2.getWidth());
        if (width == 0) {
            width = drawer.textMetrics("0", size).getWidth();
        }

//        formula1.drawAligned(drawer, x + width / 2 - metrics.getWidth() / 2, y, size, FormulaDrawer.Align.BOTTOM);
//        formula2.drawAligned(drawer, x + width / 2 - metrics2.getWidth() / 2, y, size, FormulaDrawer.Align.TOP);
        formula1.draw(drawer, x + width / 2 - metrics.getWidth() / 2, y - metrics.getHeightDown(), size);
        formula2.draw(drawer, x + width / 2 - metrics2.getWidth() / 2, y + metrics2.getHeightUp(), size);

        drawer.drawLine(x, y, x + width, y);

        metrics.setWidth(width);
        metrics.setHeightUp(metrics.getHeight());
        metrics.setHeightDown(metrics2.getHeight());

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        storedSize = size;

        Metrics metrics = formula1.calculateMetrics(drawer, size);
        Metrics metrics2 = formula2.calculateMetrics(drawer, size);

        int width = Math.max(metrics.getWidth(), metrics2.getWidth());
        if (width == 0) {
            width = drawer.textMetrics("0", size).getWidth();
        }

        metrics.setWidth(width);
        metrics.setHeightUp(metrics.getHeight());
        metrics.setHeightDown(metrics2.getHeight());

        return metrics;
    }

    @Override
    public void reMeasureCursor(Drawer drawer, Cursor cursor) {
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
    public Cursor getCursor(int position) {
        if (position == 0) {
            return new Cursor(this, 0);
        } else {
            return new Cursor(this, 1);
        }
    }

    @Override
    public Cursor getFirst() {
        return getCursor(0);
    }

    @Override
    public Cursor getLast() {
        return getCursor(1);
    }

    @Override
    public Cursor getLeft(int oldPosition) {
        if (oldPosition == 1) {
            return formula1.getLast();
        }
        return parent.getLeft(this);
    }

    @Override
    public Cursor getRight(int oldPosition) {
        if (oldPosition == 0) {
            return formula1.getFirst();
        }
        return parent.getRight(this);
    }

    @Override
    public Cursor childAsksLeft(Formula child) {
        return getFirst();
    }

    @Override
    public Cursor childAsksRight(Formula child) {
        return getLast();
    }

    @Override
    public Cursor childAsksUp(Formula child) {
        if (child == formula2) {
            return formula1.getFirst();
        }
        return super.childAsksUp(child);
    }

    @Override
    public Cursor childAsksDown(Formula child) {
        if (child == formula1) {
            return formula2.getFirst();
        }
        return super.childAsksDown(child);
    }

    @Override
    public void invalidatePlaces(Formula source) {
        super.invalidatePlaces(source);
        if (formula1 != source) {
            formula1.invalidatePlaces(this);
        }
        if (formula2 != source) {
            formula2.invalidatePlaces(this);
        }
    }

    @Override
    public void invalidateMetrics() {
        super.invalidateMetrics();
        formula1.invalidateMetrics();
        formula2.invalidateMetrics();
    }
}
