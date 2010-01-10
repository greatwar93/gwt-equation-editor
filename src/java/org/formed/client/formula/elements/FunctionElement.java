/*
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

import org.formed.client.formula.*;

/**
 *
 * @author bulats
 */
public final class FunctionElement extends PoweredElement {

    private String name;
    private Formula formula;
    private static final RightCloser right = new RightCloser();
    private static final LeftCloser left = new LeftCloser();

    public FunctionElement(String name) {
        setName(name);
        setFormula(new Formula());
    }

    public FunctionElement(String name, Formula formula) {
        setName(name);
        setFormula(formula);
    }

    public FunctionElement(String name, Formula formula, Formula power) {
        super(power);
        setName(name);
        setFormula(formula);
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
            this.formula = new Formula();
        } else {
            this.formula = formula;
        }
        formula.setParent(this);
    }

    @Override
    public Metrics draw(FormulaDrawer drawer, int x, int y, int size) {
        Metrics metrics = super.draw(drawer, x, y, size);

        if (formula != null) {
            if (formula.isComplex()) {
                metrics.add(left.draw(drawer, x + metrics.getWidth(), y, size));
                metrics.add(formula.draw(drawer, x + metrics.getWidth(), y, size));
                metrics.add(right.draw(drawer, x + metrics.getWidth(), y, size));
            } else {
                metrics.add(formula.draw(drawer, x + metrics.getWidth(), y, size));
            }
        }

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(FormulaDrawer drawer, int size) {
        Metrics metrics = super.measure(drawer, size);

        if (formula != null) {
            if (formula.isComplex()) {
                metrics.add(left.measure(drawer, size));
                metrics.add(formula.calculateMetrics(drawer, size));
                metrics.add(right.measure(drawer, size));
            } else {
                metrics.add(formula.calculateMetrics(drawer, size));
            }
        }

        return metrics;
    }

    @Override
    public Cursor getLast(FormulaDrawer drawer) {
        if (formula != null) {
            return formula.getLast(drawer);
        }
        return super.getLast(drawer);
    }

    @Override
    public Cursor childAsksLeft(FormulaDrawer drawer, Formula child) {
        if (child == formula) {
            return super.getLast(drawer);
        }

        return super.childAsksLeft(drawer, child);
    }

    @Override
    public Cursor childAsksRight(FormulaDrawer drawer, Formula child) {
        if (child == formulaPower) {
            return formula.getFirst(drawer);
        }

        return super.childAsksRight(drawer, child);
    }

    @Override
    public Cursor childAsksDown(FormulaDrawer drawer, Formula child) {
        if (child == formulaPower) {
            return super.getLast(drawer);
        }
        return super.childAsksDown(drawer, child);
    }

    @Override
    public Cursor getRight(FormulaDrawer drawer, int oldPosition) {
        if (oldPosition >= val.length()) {
            if (formula != null) {
                return formula.getFirst(drawer);
            }
        }

        return super.getRight(drawer, oldPosition);
    }

    @Override
    public void invalidateMetrics(Formula child) {
        super.invalidateMetrics(child);
        if (formula != child) {
            formula.invalidateMetrics(this);
        }
    }

    @Override
    public Cursor insertChar(FormulaDrawer drawer, Cursor cursor, char c) {
        int pos = cursor.getPosition();
        setName(name.substring(0, pos) + c + name.substring(pos));
        return getCursor(drawer, pos + 1);
    }
}