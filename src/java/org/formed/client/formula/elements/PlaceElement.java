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

import org.formed.client.formula.Cursor;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Metrics;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class PlaceElement extends BaseElement {

    public PlaceElement() {
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public Metrics draw(Drawer drawer, int x, int y, int size) {
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
    public Metrics measure(Drawer drawer, int size) {
        storedSize = size;
//        return new Metrics(0, 0, 0);

        Metrics metrics = drawer.textMetrics(".", size);
        metrics.setWidth(0);
        metrics.setHeightUp(0);
        return metrics;
    }
/*
    @Override
    public void reMeasureCursor(Drawer drawer, Cursor cursor) {
        Metrics metrics = drawer.textMetrics(".", storedSize);
        cursor.setHeightUp(0);
        cursor.setHeightDown(metrics.getHeightDown());
    }
*/
    @Override
    public Cursor insertChar(Drawer drawer, Cursor cursor, char c) {
        parent.add(new SimpleElement(""+c));
        return parent.getLast(drawer);
    }

}
