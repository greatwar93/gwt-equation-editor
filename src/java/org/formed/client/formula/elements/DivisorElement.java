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
public final class DivisorElement extends BaseElement {

    private Formula formula1;
    private Formula formula2;

    public DivisorElement() {
        setFormula1(new Formula());
        setFormula2(new Formula());
    }

    public DivisorElement(Formula formula1, Formula formula2) {
        setFormula1(formula1);
        setFormula2(formula2);
    }

    public Formula getFormula1() {
        return formula1;
    }

    public void setFormula1(Formula formula1) {
        if (formula1 == null) {
            this.formula1 = new Formula();
        } else {
            this.formula1 = formula1;
        }
        formula1.setParent(this);
    }

    public Formula getFormula2() {
        return formula2;
    }

    public void setFormula2(Formula formula2) {
        if (formula2 == null) {
            this.formula2 = new Formula();
        } else {
            this.formula2 = formula2;
        }
        formula2.setParent(this);
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public Metrics draw(FormulaDrawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        Metrics metrics = formula1.calculateMetrics(drawer, size);
        Metrics metrics2 = formula2.calculateMetrics(drawer, size);

        int width = Math.max(metrics.getWidth(), metrics2.getWidth());
//        formula1.drawAligned(drawer, x + width / 2 - metrics.getWidth() / 2, y, size, FormulaDrawer.Align.BOTTOM);
//        formula2.drawAligned(drawer, x + width / 2 - metrics2.getWidth() / 2, y, size, FormulaDrawer.Align.TOP);
        formula1.draw(drawer, x + width / 2 - metrics.getWidth() / 2, y - metrics.getHeightDown(), size);
        formula2.draw(drawer, x + width / 2 - metrics2.getWidth() / 2, y + metrics2.getHeightUp(), size);

        drawer.getCanvas().beginPath();
        drawer.getCanvas().moveTo(x, y);
        drawer.getCanvas().lineTo(x + width, y);
        drawer.getCanvas().stroke();

        metrics.setWidth(width);
        metrics.setHeightUp(metrics.getHeight());
        metrics.setHeightDown(metrics2.getHeight());

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(FormulaDrawer drawer, int size) {
        storedSize = size;

        Metrics metrics = formula1.calculateMetrics(drawer, size);
        Metrics metrics2 = formula2.calculateMetrics(drawer, size);

        int width = Math.max(metrics.getWidth(), metrics2.getWidth());

        metrics.setWidth(width);
        metrics.setHeightUp(metrics.getHeight());
        metrics.setHeightDown(metrics2.getHeight());

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
            return formula1.getLast(drawer);
        }
        return parent.getLeft(drawer, this);
    }

    @Override
    public CursorPosition getRight(FormulaDrawer drawer, int oldPosition) {
        if (oldPosition == 0) {
            return formula1.getFirst(drawer);
        }
        return parent.getRight(drawer, this);
    }

    @Override
    public CursorPosition childAsksLeft(FormulaDrawer drawer, Formula child) {
        return getFirst(drawer);
    }

    @Override
    public CursorPosition childAsksRight(FormulaDrawer drawer, Formula child) {
        return getLast(drawer);
    }

    @Override
    public CursorPosition childAsksUp(FormulaDrawer drawer, Formula child) {
        if(child == formula2){
            return formula1.getFirst(drawer);
        }
        return super.childAsksUp(drawer, child);
    }

    @Override
    public CursorPosition childAsksDown(FormulaDrawer drawer, Formula child) {
        if(child == formula1){
            return formula2.getFirst(drawer);
        }
        return super.childAsksDown(drawer, child);
    }

    @Override
    public void invalidateMetrics(Formula child) {
        super.invalidateMetrics(child);
        if (formula1 != child) {
            formula1.invalidateMetrics(this);
        }
        if (formula2 != child) {
            formula2.invalidateMetrics(this);
        }
    }
}
