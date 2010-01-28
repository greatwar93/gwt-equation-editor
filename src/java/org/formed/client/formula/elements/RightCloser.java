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

import org.formed.client.formula.Drawer;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class RightCloser extends PoweredElement {

    public RightCloser() {
        val = ")";
    }

    public RightCloser(Formula power) {
        super(power);
        val = ")";
    }

    public FormulaItem makeClone() {
        RightCloser clone = new RightCloser(getPower().makeClone());
        clone.setParent(parent);

        return clone;
    }

    public FormulaItem getLeftCloser(){
        if(parent == null) return null;
        return parent.findLeftCloser(this);
    }
/*
    protected int newSize(Drawer drawer, int size) {
        if (parent == null) {
            return size;
        }

        int posTo = parent.getItemPosition(this);
        int posFrom = parent.findLeftCloser(posTo);
        Metrics metrics = parent.findMaxHeights(drawer, size, posFrom+1, posTo - posFrom - 1);

        return drawer.sizeForHeight(val, metrics.getHeight());
    }

    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
        size = newSize(drawer, size);

        Metrics metrics = measure(drawer, size);

        drawer.drawText(val, size, x, y);

        return metrics;
    }

    @Override
    public Metrics measure(Drawer drawer, int size) {
        if (parent == null) {
            return super.measure(drawer, size);
        }

        int posTo = parent.getItemPosition(this);
        int posFrom = parent.findLeftCloser(posTo);
        Metrics metrics = parent.findMaxHeights(drawer, size, posFrom + 1, posTo - posFrom - 1);

        size = drawer.sizeForHeight(val, metrics.getHeight());
//        return drawer.sizeForHeight(val, metrics.getHeight());

        return drawer.textMetrics(val, size);
    }*/
}
