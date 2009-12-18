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

import org.formed.client.formula.FormulaDrawer;
import org.formed.client.formula.Metrics;

/**
 *
 * @author bulats
 */
public class PlaceElement extends BaseElement {

    public PlaceElement() {
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public Metrics draw(FormulaDrawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        drawer.addDrawnItem(this, x, y, drawer.textMetrics(".", size));
        return new Metrics(0, 0, 0);
/*        Metrics metrics = drawer.textMetrics(".", size);

        drawer.addDrawnItem(this, x, y, metrics);

        metrics.setWidth(0);
        return metrics;*/
    }

    @Override
    public Metrics measure(FormulaDrawer drawer, int size) {
        storedSize = size;
//        return new Metrics(0, 0, 0);

        Metrics metrics = drawer.textMetrics(".", size);
        metrics.setWidth(0);
        metrics.setHeightUp(0);
        return metrics;
    }


}
