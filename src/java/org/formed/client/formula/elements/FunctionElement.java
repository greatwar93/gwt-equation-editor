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
public final class FunctionElement extends PoweredElement {

    private String name;
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
        FunctionElement clone = new FunctionElement(name, formula.makeClone(), parent.makeClone());
        clone.setParent(parent);

        return clone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        val = name;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        if (formula == null) {
            this.formula = new Formula(true);
        } else {
            this.formula = formula;
            formula.setShowPlace(true);
        }
        formula.setParent(this);
    }

    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
        Metrics metrics = measure(drawer, size);

        if (highlighted) {
            drawer.fillRect(x, y - metrics.getHeightUp(), x + metrics.getWidth(), y + metrics.getHeightDown(), highlightR, highlightG, highlightB);
        }

        metrics = super.draw(drawer, x, y, size);
        metrics.setWidth(metrics.getWidth() + 2);

        if (formula != null) {
            /*            if (formula.isComplex()) {
            metrics.add(left.draw(drawer, x + metrics.getWidth(), y, size));
            metrics.add(formula.draw(drawer, x + metrics.getWidth(), y, size));
            metrics.add(right.draw(drawer, x + metrics.getWidth(), y, size));
            } else {*/
            metrics.add(formula.draw(drawer, x + metrics.getWidth(), y, size));
//            }
        }

        if (strokeThrough) {
            drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
        }

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        Metrics metrics = super.measure(drawer, size);
        metrics.setWidth(metrics.getWidth() + 2);

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
    public boolean isYourEnd(Cursor cursor) {
        return cursor.getItem() == this && cursor.getPosition() == -1;
    }

    @Override
    public Cursor getLast() {
        return formula.getLast();
        /*        if (formula != null) {
        return formula.getLast();
        }
        return super.getLast();*/
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

        return super.getLeft(oldPosition);
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
    public void reMeasureCursor(Drawer drawer, Cursor cursor) {
        if (cursor.getPosition() == -1) {
            Metrics metrics = measure(drawer, storedSize);
            cursor.setX(storedX + metrics.getWidth());
            cursor.setY(storedY);
            cursor.setHeightUp(metrics.getHeightUp());
            cursor.setHeightDown(metrics.getHeightDown());
        } else {
            super.reMeasureCursor(drawer, cursor);
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
    public Cursor insertChar(Cursor cursor, char c) {
        int pos = cursor.getPosition();
        setName(name.substring(0, pos) + c + name.substring(pos));
        return getCursor(pos + 1);
    }
}
