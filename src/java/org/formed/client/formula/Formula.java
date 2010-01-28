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
package org.formed.client.formula;

import java.util.ArrayList;
import java.util.List;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.PlaceElement;
import org.formed.client.formula.elements.RightCloser;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class Formula {

    public static final Formula ZERO_FORMULA = new Formula();
    private PlaceElement place = new PlaceElement();
    private final List<FormulaItem> items = new ArrayList<FormulaItem>();
    private FormulaItem parent = null;
    private boolean metricsValid = false;
    private Metrics metrics = new Metrics(0, 0, 0);

    public Formula() {
        place.setParent(this);
    }

    public Formula(boolean showPlace) {
        place.setShow(showPlace);
        place.setParent(this);
    }

    public Formula makeClone() {
        Formula clone = new Formula();
        clone.setParent(parent);
        for (FormulaItem item : items) {
            clone.add(item.makeClone());
        }

        return clone;
    }

    public void setShowPlace(boolean showPlace){
        place.setShow(showPlace);
    }

    public FormulaItem getParent() {
        return parent;
    }

    public void setParent(FormulaItem parent) {
        this.parent = parent;
    }

    public void invalidateMetrics() {
        metricsValid = false;
        for (FormulaItem item : items) {
            item.invalidateMetrics();
        }
    }

    public void invalidatePlaces() {
        invalidatePlaces(null);
        /*        metricsValid = false;
        if (parent != null) {
        parent.invalidatePlaces(this);
        }*/
    }

    public void invalidatePlaces(FormulaItem child) {
        /*        metricsValid = false;
        
        boolean found = (child != null);
        for(FormulaItem item : items){
        if(found){
        item.invalidatePlaces(this);
        }else if(item == child){
        found = true;
        }
        }

        if (child != parent && parent != null) {
        parent.invalidatePlaces(this);
        }*/
    }

    public Metrics drawAligned(Drawer drawer, int x, int y, int size, Drawer.Align align) {
        switch (align) {
            case TOP:
                calculateMetrics(drawer, size);
                return draw(drawer, x, y + metrics.getHeightUp(), size);
            case BOTTOM:
                calculateMetrics(drawer, size);
                return draw(drawer, x, y - metrics.getHeightDown(), size);
            case MIDDLE:
                return draw(drawer, x, y, size);
            default:
                return draw(drawer, x, y, size);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isComplex() {
        if (items.size() > 1) {
            return true;
        }

        if (items.size() > 0) {
            return items.get(0).isComplex();
        }

        return false;
    }

    public int getItemsCount(){
        return items.size();
    }

    public Metrics draw(Drawer drawer, int x, int y, int size) {
        Metrics drawnMetrics = new Metrics(0, 0, 0);
        if (items.isEmpty()) {
            drawer.drawDebugText("E" + items.size());
            drawnMetrics.add(place.draw(drawer, x + drawnMetrics.getWidth(), y, size));
        } else {
            drawer.drawDebugText("" + items.size());
            for (FormulaItem item : items) {
                drawnMetrics.add(item.draw(drawer, x + drawnMetrics.getWidth(), y, size));
                drawnMetrics.setWidth(drawnMetrics.getWidth() + 1);
            }
        }

        drawer.addDrawnFormula(this, new Rectangle(x, y - drawnMetrics.getHeightUp(), drawnMetrics.getWidth(), drawnMetrics.getHeight()));

        drawer.addDrawnFormula(this, x, y, drawnMetrics);
        return drawnMetrics;
    }

    public Metrics calculateMetrics(Drawer drawer, int size) {
        if (metricsValid) {
            return metrics.cloneMetrics();
        }

        metrics.clear();
        if (items.isEmpty()) {
            metrics.add(place.measure(drawer, size));
        } else {
            for (FormulaItem item : items) {
                metrics.add(item.measure(drawer, size));
            }
        }

        metrics.setWidth(metrics.getWidth() + items.size());
        metricsValid = true;

        return metrics.cloneMetrics();
    }

    public Formula add(FormulaItem item) {
        item.setParent(this);
        items.add(item);
        invalidatePlaces();
        return this;
    }

    public Formula insertAt(int position, FormulaItem item) {
        if (position >= 0 && position <= items.size()) {
            item.setParent(this);
            items.add(position, item);
            invalidatePlaces();
        }
        return this;
    }

    public Formula insertAfter(FormulaItem item, FormulaItem after) {
        if (items.indexOf(after) >= 0) {
            item.setParent(this);
            items.add(items.indexOf(after) + 1, item);
            invalidatePlaces();
        } else if (after == place) {
            item.setParent(this);
            items.add(item);
            invalidatePlaces();
        }
        return this;
    }

    public Formula insertBefore(FormulaItem item, FormulaItem before) {
        if (items.indexOf(before) >= 0) {
            item.setParent(this);
            items.add(items.indexOf(before), item);
            invalidatePlaces();
        } else if (before == place) {
            item.setParent(this);
            items.add(item);
            invalidatePlaces();
        }
        return this;
    }

    public Formula replace(FormulaItem newItem, FormulaItem oldItem) {
        if (items.indexOf(oldItem) >= 0) {
            oldItem.setParent(null);
            newItem.setParent(this);
            items.set(items.indexOf(oldItem), newItem);
            invalidatePlaces();
        } else if (oldItem == place) {
            newItem.setParent(this);
            items.add(newItem);
            invalidatePlaces();
        }
        return this;
    }

    public Formula remove(FormulaItem item) {
        items.remove(item);
        item.setParent(null);
        return this;
    }

    public Cursor removeLeft(FormulaItem item) {
        int index = items.indexOf(item);
        if (index > 0) {
            items.remove(index - 1);
        }

        if (items.size() > 0) {
            return items.get(index - 1).getLast();
        } else {
            return getFirst();
        }
    }

    public Cursor removeRight(FormulaItem item) {
        int index = items.indexOf(item);
        if (index < items.size()) {
            items.remove(index + 1);
            return item.getLast();
        } else {
            return getLast();
        }
    }

    public Formula removeAt(int position) {
        if (position >= 0 && position < items.size()) {
            FormulaItem item = items.get(position);
            items.remove(position);
            item.setParent(null);
            invalidatePlaces();
        }
        return this;
    }

    //Is specified item first in formula
    public boolean isFirst(FormulaItem item) {
        return (items.indexOf(item) == 0);
    }

    //Get position when come from child-item
    public Cursor getLeft(FormulaItem item) {
        int index = items.indexOf(item) - 1;
        if (index < 0) {
            if (parent != null) {
                return parent.childAsksLeft(this);
            }
            return null;
        }
        return items.get(index).getLast();
    }

    //Get position when come from child-item
    public Cursor getRight(FormulaItem item) {
        int index = items.indexOf(item) + 1;
        if (index >= items.size()) {
            if (parent != null) {
                return parent.childAsksRight(this);
            }
            return null;
        }
        return items.get(index).getFirst();
    }

    public Cursor getYourRight(FormulaItem item) {
        int index = items.indexOf(item) + 1;
        if (index >= items.size()) {
            return null;
        }
        return items.get(index).getFirst();
    }

    public Cursor getYourLeft(FormulaItem item) {
        int index = items.indexOf(item) - 1;
        if (index < 0) {
            return null;
        }
        return items.get(index).getLast();
    }

    //Get position when come from child-item
    public Cursor getUp(FormulaItem item) {
        if (parent == null) {
            return null;
        }
        return parent.childAsksUp(this);
    }

    //Get position when come from child-item
    public Cursor getDown(FormulaItem item) {
        if (parent == null) {
            return null;
        }
        return parent.childAsksDown(this);
    }

    public FormulaItem getItem(int position) {
        return items.get(position);
    }

    public int getItemPosition(FormulaItem item) {
        return items.indexOf(item);
    }

    public FormulaItem getFirstItem() {
        if (items.isEmpty()) {
            return place;
        }

        return items.get(0);
    }

    public FormulaItem getLastItem() {
        if (items.isEmpty()) {
            return place;
        }

        return items.get(items.size() - 1);
    }

    public FormulaItem getRightItem(FormulaItem item) {
        int index = items.indexOf(item) + 1;
        if (index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public FormulaItem getLeftItem(FormulaItem item) {
        int index = items.indexOf(item) - 1;
        if (index < 0) {
            return null;
        }
        return items.get(index);
    }

    public Cursor getFirst() {
        return getFirstItem().getFirst();
    }

    public Cursor getLast() {
        return getLastItem().getLast();
    }

    /*
     * Find the position of a LeftCloser that corresponds to the RightCloser in a specified position.
     */
    public int findLeftCloser(int posTo) {
        int closers = 0;
        for (int pos = posTo; pos > 0; pos--) {
            FormulaItem posItem = items.get(pos);
            if (posItem instanceof LeftCloser) {
                closers--;
                if (closers <= 0) {
                    return pos;
                }
            } else if (posItem instanceof RightCloser) {
                closers++;
            }
        }

        return 0;
    }

    /*
     * Find the position of a RightCloser that corresponds to the LeftCloser in a specified position.
     */
    public int findRightCloser(int posFrom) {
        int size = items.size();
        int closers = 0;
        for (int pos = posFrom; pos < size; pos++) {
            FormulaItem posItem = items.get(pos);
            if (posItem instanceof LeftCloser) {
                closers++;
            } else if (posItem instanceof RightCloser) {
                closers--;
                if (closers <= 0) {
                    return pos;
                }
            }
        }

        return size - 1;
    }
}
