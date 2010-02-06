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

import org.formed.client.formula.editor.Cursor;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.drawer.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public abstract class PoweredElement extends BaseElement {

    private Formula formulaPower;

    public PoweredElement(Formula power) {
        setPower(power);
    }

    public PoweredElement() {
        setPower(new Formula());
    }

    public PoweredElement(Formula power, boolean strokeThrough) {
        this.strokeThrough = strokeThrough;
        setPower(power);
    }

    public PoweredElement(boolean strokeThrough) {
        this.strokeThrough = strokeThrough;
        setPower(new Formula());
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && formulaPower.isEmpty();
    }

    public boolean isComplex() {
        return !formulaPower.isEmpty();
    }

    @Override
    public boolean isYouOrInsideYou(FormulaItem item) {
        if (super.isYouOrInsideYou(item)) {
            return true;
        }
        if (formulaPower.isInsideYou(item)) {
            return true;
        }
        return false;
    }

    public Formula getPower() {
        return formulaPower;
    }

    public void setPower(Formula power) {
        if (power == null) {
            this.formulaPower = new Formula();
        } else {
            this.formulaPower = power;
        }
        this.formulaPower.setParent(this);
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

        drawer.drawText(val, size, x, y);
        metrics = super.measure(drawer, size);
        drawer.addDrawnItem(this, x, y, metrics);

        boolean stroked = false;
        if (formulaPower != null) {
            if (formulaPower.isEmpty()) {
                if (strokeThrough) {
                    stroked = true;
                    drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
                }
            }

            int powerSize = drawer.getSmallerSize(size);
            Metrics powerMetrics = formulaPower.calculateMetrics(drawer, powerSize);
            int powerX = 0;
            int powerY = -metrics.getHeightUp() - powerMetrics.getHeightDown();

            formulaPower.draw(drawer, x + metrics.getWidth() + powerX, y + powerY, powerSize);
            metrics.add(powerMetrics, powerX, powerY);
        }

        if (strokeThrough && !stroked) {
            drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
        }

        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        Metrics metrics = super.measure(drawer, size);

        if (formulaPower != null) {
            int powerSize = drawer.getSmallerSize(size);
            Metrics powerMetrics = formulaPower.calculateMetrics(drawer, powerSize);
            int powerX = 0;
            int powerY = -metrics.getHeightUp() - powerMetrics.getHeightDown();

            metrics.add(powerMetrics, powerX, powerY);
        }

        return metrics;
    }

    @Override
    public Cursor getUp(int oldPosition) {
        if (formulaPower != null) {
            return formulaPower.getFirst();
        }
        return super.getUp(oldPosition);
    }

    @Override
    public Cursor childAsksLeft(Formula child) {
        if (child == formulaPower) {
            return getLast();
        } else {
            return super.childAsksLeft(child);
        }
    }

    @Override
    public Cursor childAsksRight(Formula child) {
        if (child == formulaPower) {
            return getLast();
        } else {
            return super.childAsksRight(child);
        }
    }

    @Override
    public Cursor childAsksDown(Formula child) {
        if (child == formulaPower) {
            return getLast();
        }
        return super.childAsksDown(child);
    }

    @Override
    public void invalidatePlaces(Formula source) {
        super.invalidatePlaces(source);
        if (formulaPower != source) {
            formulaPower.invalidatePlaces(this);
        }
    }

    @Override
    public void invalidateMetrics() {
        super.invalidateMetrics();
        formulaPower.invalidateMetrics();
    }
}
