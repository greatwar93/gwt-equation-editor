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
public final class RootElement extends BaseElement {

    private Formula formula;
    private Formula formulaPower;

    public RootElement(Formula formula) {
        setFormula(formula);
        setFormulaPower(new Formula());
    }

    public RootElement(Formula formula, Formula formulaPower) {
        setFormula(formula);
        setFormulaPower(formulaPower);
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

    public boolean isComplex() {
        return false;
    }

    @Override
    public Metrics draw(FormulaDrawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        Metrics metrics = new Metrics(4, 0, 0);
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

        drawer.getCanvas().beginPath();
        drawer.getCanvas().moveTo(x, y);
        drawer.getCanvas().lineTo(x + metrics.getWidth(), y);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 2, y + metrics2.getHeightDown() - 1);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 4, y - metrics2.getHeightUp() - 1);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() - 1);
        drawer.getCanvas().lineTo(x + metrics.getWidth() + 6 + metrics2.getWidth(), y - metrics2.getHeightUp() + 2);
        drawer.getCanvas().stroke();

        metrics.setWidth(metrics.getWidth() + metrics2.getWidth() + 6);
        metrics.setHeightUp(Math.max(metrics2.getHeightUp() + 4, metrics.getHeight() + metrics2.getHeight() / 2));
        metrics.setHeightDown(metrics2.getHeightDown());

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(FormulaDrawer drawer, int size) {
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
    public CursorPosition getCursor(FormulaDrawer drawer, int x, int y) {
        Metrics metrics = measure(drawer, storedSize);
        if (x - storedX < metrics.getWidth() / 2) {
            return new CursorPosition(this, 0, storedX, storedY, metrics.getHeightUp(), metrics.getHeightDown());
        } else {
            return new CursorPosition(this, 1, storedX + metrics.getWidth(), storedY, metrics.getHeightUp(), metrics.getHeightDown());
        }
    }

    @Override
    public CursorPosition getCursor(FormulaDrawer drawer, int position) {
        Metrics metrics = measure(drawer, storedSize);
        if (position == 0) {
            return new CursorPosition(this, 0, storedX, storedY, metrics.getHeightUp(), metrics.getHeightDown());
        } else {
            return new CursorPosition(this, 1, storedX + metrics.getWidth(), storedY, metrics.getHeightUp(), metrics.getHeightDown());
        }
    }

    @Override
    public CursorPosition getFirst(FormulaDrawer drawer) {
        return getCursor(drawer, 0);
    }

    @Override
    public CursorPosition getLast(FormulaDrawer drawer) {
        return getCursor(drawer, 1);
    }

    @Override
    public CursorPosition getLeft(FormulaDrawer drawer, int oldPosition) {
        if (oldPosition == 1) {
            return formula.getLast(drawer);
        }

        return parent.getLeft(drawer, this);
    }

    @Override
    public CursorPosition getRight(FormulaDrawer drawer, int oldPosition) {
        if (oldPosition == 0) {
            return formulaPower.getFirst(drawer);
        }

        return parent.getRight(drawer, this);
    }

    @Override
    public CursorPosition childAsksLeft(FormulaDrawer drawer, Formula child) {
        if (child == formula) {
            return formulaPower.getLast(drawer);
        }

        return getFirst(drawer);
    }

    @Override
    public CursorPosition childAsksRight(FormulaDrawer drawer, Formula child) {
        if (child == formulaPower) {
            return formula.getFirst(drawer);
        }

        return getLast(drawer);
    }

    @Override
    public CursorPosition childAsksUp(FormulaDrawer drawer, Formula child) {
        if(child == formula){
            return formulaPower.getLast(drawer);
        }
        return super.childAsksUp(drawer, child);
    }

    @Override
    public CursorPosition childAsksDown(FormulaDrawer drawer, Formula child) {
        if(child == formulaPower){
            return formula.getFirst(drawer);
        }
        return super.childAsksDown(drawer, child);
    }


    @Override
    public void invalidateMetrics(Formula child) {
        super.invalidateMetrics(child);
        if (formula != child) {
            formula.invalidateMetrics(this);
        }
        if (formulaPower != child) {
            formulaPower.invalidateMetrics(this);
        }
    }
}
