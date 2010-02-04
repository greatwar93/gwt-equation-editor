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
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.FormulaItem.HowToInsert;
import org.formed.client.formula.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class OperatorElement extends BaseElement {

    //private String name;

    public OperatorElement(String name) {
        //this.name = name;
        val = name;
    }

    public FormulaItem makeClone() {
//        OperatorElement clone = new OperatorElement(name);
        OperatorElement clone = new OperatorElement(val);
        clone.setParent(parent);
        
        return clone;
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isIncorporatable() {
        return false;
    }

    public String getName() {
        return val;
//        return name;
    }

    public void setName(String name) {
//        this.name = name;
        val = name;
    }


    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
        Metrics metrics = measure(drawer, size);

        if (highlighted) {
            drawer.fillRect(x, y - metrics.getHeightUp(), x + metrics.getWidth(), y + metrics.getHeightDown(), highlightR, highlightG, highlightB);
        }

        metrics = super.draw(drawer, x+2, y, size);
        metrics.setWidth(metrics.getWidth() + 4);

        if (strokeThrough) {
            drawer.drawLine(x, y + metrics.getHeightDown(), x + metrics.getWidth(), y - metrics.getHeightUp());
        }

        drawer.addDrawnItem(this, x, y, metrics);
        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        Metrics metrics = super.measure(drawer, size);
        metrics.setWidth(metrics.getWidth() + 4);

        return metrics;
    }

    @Override
    public HowToInsert getHowToInsert(Cursor cursor, FormulaItem item) {
        if(item == null || cursor == null) return HowToInsert.NONE;

        return cursor.getPosition() <= 0 ? HowToInsert.LEFT : HowToInsert.RIGHT;
    }

}
