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
public abstract class PoweredElement extends BaseElement {

    private Formula formulaPower;

    public PoweredElement(Formula power) {
        setPower(power);
    }

    public PoweredElement() {
        setPower(new Formula());
    }

    public boolean isComplex() {
        return !formulaPower.isEmpty();
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
        Metrics metrics = super.draw(drawer, x, y, size);

        if (formulaPower != null) {
            int powerSize = drawer.getSmallerSize(size);
            int powerX = 0;
            int powerY = -metrics.getHeightUp();

            formulaPower.draw(drawer, x + metrics.getWidth() + powerX, y + powerY, powerSize);
            metrics.add(formulaPower.calculateMetrics(drawer, powerSize), powerX, powerY);
        }

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        Metrics metrics = super.measure(drawer, size);

        if (formulaPower != null) {
            int powerSize = drawer.getSmallerSize(size);
            int powerX = 0;
            int powerY = -metrics.getHeightUp();

            metrics.add(formulaPower.calculateMetrics(drawer, powerSize), powerX, powerY);
        }

        return metrics;
    }

    @Override
    public Cursor getUp(Drawer drawer, int oldPosition) {
        if(formulaPower != null){
            return formulaPower.getFirst(drawer);
        }
        return super.getUp(drawer, oldPosition);
    }

    @Override
    public Cursor childAsksLeft(Drawer drawer, Formula child) {
        if (child == formulaPower) {
            return getLast(drawer);
        } else {
            return super.childAsksLeft(drawer, child);
        }
    }

    @Override
    public Cursor childAsksDown(Drawer drawer, Formula child) {
        if(child == formulaPower){
            return getLast(drawer);
        }
        return super.childAsksDown(drawer, child);
    }

    @Override
    public void invalidateMetrics(Formula child) {
        super.invalidateMetrics(child);
        if (formulaPower != child) {
            formulaPower.invalidateMetrics(this);
        }
    }
}
