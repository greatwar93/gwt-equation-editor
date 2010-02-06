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

import org.formed.client.formula.drawer.Metrics;
import org.formed.client.formula.drawer.Rectangle;
import org.formed.client.formula.editor.Cursor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.formed.client.formula.elements.LeftCloser;
import org.formed.client.formula.elements.PlaceElement;
import org.formed.client.formula.elements.RightCloser;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class Formula {

    public static final Formula ZERO_FORMULA = new Formula();
    private final PlaceElement place = new PlaceElement();
    private boolean autoBrackets = false;
    private final List<FormulaItem> items = new ArrayList<FormulaItem>();
    private FormulaItem parent = null;
    private boolean metricsValid = false;
    private int storedSize = 0;
    private Metrics metrics = new Metrics(0, 0, 0);
    private final Map<FormulaItem, List<FormulaItem>> addedMap = new HashMap<FormulaItem, List<FormulaItem>>();

    public Formula() {
        place.setParent(this);
    }

    public Formula(boolean showPlace) {
        place.setShow(showPlace);
        place.setParent(this);
    }

    public Formula(boolean showPlace, boolean autoBrackets) {
        place.setShow(showPlace);
        place.setParent(this);
        setAutoBrackets(autoBrackets);
    }

    /**
     * Create formula clone
     * @return a newly created formula clone
     */
    public Formula makeClone() {
        Formula clone = new Formula();
        clone.setParent(parent);
        for (FormulaItem item : items) {
            clone.add(item.makeClone());
        }

        return clone;
    }

    /**
     * Set whether to show placeholder for empty formula or not
     * @param showPlace true - show placeholder for empty formula, false - do not show placeholder for empty formula
     */
    public void setShowPlace(boolean showPlace) {
        place.setShow(showPlace);
    }

    /**
     * Set whether to enclose formula into brackets automatically when it became complex or not.
     * Used for formulas inside functions.
     * @param autoBrackets true - enclose into brackets automatically, false - do not
     */
    public void setAutoBrackets(boolean autoBrackets) {
        this.autoBrackets = autoBrackets;
    }

    /**
     * Returns whether to enclose formula into brackets automatically when it became complex or not.
     * Used for formulas inside functions.
     * @return true - enclose into brackets automatically, false - do not
     */
    private boolean isAutoBrackets() {
        return false;
        //return autoBrackets;
    }

    /**
     * Returns parent element
     * @return parent element, null if no parent
     */
    public FormulaItem getParent() {
        return parent;
    }

    /**
     * Set parent element
     * @param parent element to be set as a parent for this formula
     */
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

    /**
     * 
     * @param drawer
     * @param x
     * @param y
     * @param size
     * @param align
     * @return
     */
    public Metrics drawAligned(Drawer drawer, int x, int y, int size, Drawer.Align align) {
        storedSize = size;
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

    public Metrics measureAligned(Drawer drawer, int x, int y, int size, Drawer.Align align) {
        storedSize = size;
        return calculateMetrics(drawer, size);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private boolean hasFirstLeftCloser() {
        return items.size() < 1 ? false : items.get(0) instanceof LeftCloser;
    }

    private boolean hasLastRightCloser() {
        return items.size() < 1 ? false : items.get(items.size() - 1) instanceof RightCloser;
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

    /**
     * Checks whether specified FormulaItem is inside this formula or any of it's children
     * @param insideItem FormulaItem to check
     * @return true if specified FormulaItem is inside this formula or any of it's children, false otherwise
     */
    public boolean isInsideYou(FormulaItem insideItem) {
        return insideItem == place ? true : posInsideYou(insideItem) >= 0;
    }

    /**
     * Returns position of a specified FormulaItem in this formula
     * @param insideItem FormulaItem to get position of
     * @return position of a specified FormulaItem in this formula, -1 otherwise
     */
    public int posInsideYou(FormulaItem insideItem) {
        if (insideItem == place) {
            return 0;
        }

        int pos = 0;
        for (FormulaItem item : items) {
            if (item.isYouOrInsideYou(insideItem)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public int getItemsCount() {
        return items.size();
    }

    public Metrics draw(Drawer drawer, int x, int y, int size) {
        storedSize = size;
        Metrics drawnMetrics = new Metrics(0, 0, 0);
        if (items.isEmpty()) {
            //drawer.drawDebugText("E" + items.size());
            drawnMetrics.add(place.draw(drawer, x + drawnMetrics.getWidth(), y, size));
        } else {
            //drawer.drawDebugText("" + items.size());
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
    private static int i = 0;

    private void addClosersIfNeededOnInsert(FormulaItem item) {
        int position = items.indexOf(item);
        if (isAutoBrackets() && items.size() > 1) {
            //RootPanel.get().add(new HTML(position+":"+(items.size()-1)+" "+(item instanceof LeftCloser)+":"+hasLastRightCloser()+" "+(item instanceof RightCloser)+":"+hasFirstLeftCloser()), 10, 300 + i * 30);
            //i++;
            if (position == 0 && item instanceof LeftCloser && !hasLastRightCloser()) {
                addLastCloser(new RightCloser(), item);
            } else if (position == items.size() - 1 && item instanceof RightCloser && !hasFirstLeftCloser()) {
                addFirstCloser(new LeftCloser(), item);
            } else if (!hasFirstLeftCloser() || !hasLastRightCloser()) {
                addFirstCloser(new LeftCloser(), item);
                addLastCloser(new RightCloser(), item);
            }
        }
    }

    private void addClosersIfNeededOnReplace(FormulaItem newItem, FormulaItem oldItem) {
        int position = items.indexOf(newItem);
        if (isAutoBrackets() && items.size() > 1) {

            if (oldItem instanceof LeftCloser && newItem instanceof LeftCloser) {
                return;
            }
            if (oldItem instanceof RightCloser && newItem instanceof RightCloser) {
                return;
            }

            if (position == 0 && newItem instanceof LeftCloser && !hasLastRightCloser()) {
                addLastCloser(new RightCloser(), newItem);
            } else if (position == items.size() - 1 && newItem instanceof RightCloser && !hasFirstLeftCloser()) {
                addFirstCloser(new LeftCloser(), newItem);
            } else if (!hasFirstLeftCloser() || !hasLastRightCloser()) {
                addFirstCloser(new LeftCloser(), newItem);
                addLastCloser(new RightCloser(), newItem);
            }
        }
    }

    private boolean checkClosersOnRemove(int index) {
        if (isAutoBrackets() && items.size() > 3) {
            if (index == 0 && items.get(index) instanceof LeftCloser) {
                return false;
            }
            if (index == items.size() - 1 && items.get(index) instanceof RightCloser) {
                return false;
            }
        }

        return true;
    }

    private boolean checkClosersOnRemove(FormulaItem item) {
        int index = items.indexOf(item);
        return index == -1 ? true : checkClosersOnRemove(index);
    }

    private void removeAdded(FormulaItem item) {
        List<FormulaItem> closers = addedMap.get(item);
        if (closers != null) {
            //items.removeAll(closers);
            for (FormulaItem closer : closers) {
                items.remove(closer);
                closer.setParent(null);
            }
            addedMap.remove(item);
        }
    }

    private void addFirstCloser(FormulaItem closer, FormulaItem item) {
        closer.setParent(this);
        items.add(0, closer);

        List<FormulaItem> closers = addedMap.get(item);
        if (closers == null) {
            closers = new ArrayList<FormulaItem>();
            addedMap.put(item, closers);
            RootPanel.get().add(new HTML("first"), 10, 300 + i * 30);
            i++;
        }
        closers.add(item);
    }

    private void addLastCloser(FormulaItem closer, FormulaItem item) {
        closer.setParent(this);
        items.add(closer);

        List<FormulaItem> closers = addedMap.get(item);
        if (closers == null) {
            closers = new ArrayList<FormulaItem>();
            addedMap.put(item, closers);
            RootPanel.get().add(new HTML("first"), 10, 300 + i * 30);
            i++;
        }
        closers.add(item);
    }

    public Formula add(FormulaItem item) {
        item.setParent(this);
        items.add(item);

        addClosersIfNeededOnInsert(item);

        invalidatePlaces();
        return this;
    }

    public Formula insertAt(int position, FormulaItem item) {
        if (position >= 0 && position <= items.size()) {
            item.setParent(this);
            items.add(position, item);

            addClosersIfNeededOnInsert(item);

            invalidatePlaces();
        }
        return this;
    }

    public Formula insertAfter(FormulaItem item, FormulaItem after) {
        if (after == place) {
            add(item);
        } else if (items.indexOf(after) >= 0) {
            item.setParent(this);
            items.add(items.indexOf(after) + 1, item);

            addClosersIfNeededOnInsert(item);

            invalidatePlaces();
        }
        return this;
    }

    public Formula insertBefore(FormulaItem item, FormulaItem before) {
        if (before == place) {
            add(item);
        } else if (items.indexOf(before) >= 0) {
            item.setParent(this);
            items.add(items.indexOf(before), item);

            addClosersIfNeededOnInsert(item);

            invalidatePlaces();
        }
        return this;
    }

    public Formula replace(FormulaItem newItem, FormulaItem oldItem) {
        if (items.indexOf(oldItem) >= 0) {
            oldItem.setParent(null);
            newItem.setParent(this);
            items.set(items.indexOf(oldItem), newItem);

            addClosersIfNeededOnReplace(newItem, oldItem);

            invalidatePlaces();
        } else if (oldItem == place) {
            newItem.setParent(this);
            items.add(newItem);

            addClosersIfNeededOnInsert(newItem);

            invalidatePlaces();
        }
        return this;
    }

    public Formula remove(FormulaItem item) {
        if (!checkClosersOnRemove(item)) {
            return this;
        }
        items.remove(item);
        item.setParent(null);
        removeAdded(item);
        return this;
    }

    public Cursor removeLeft(FormulaItem item) {
        int index = items.indexOf(item);

        if (index > 0) {
            if (checkClosersOnRemove(index - 1)) {
                FormulaItem removeItem = items.get(index - 1);
                items.remove(index - 1);
                removeItem.setParent(null);
                removeAdded(removeItem);
            }
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
            if (checkClosersOnRemove(index + 1)) {
                FormulaItem removeItem = items.get(index + 1);
                items.remove(index + 1);
                removeItem.setParent(null);
                removeAdded(removeItem);
            }
            return item.getLast();
        } else {
            return getLast();
        }
    }

    public Formula removeAt(int position) {
        if (position >= 0 && position < items.size()) {
            if (!checkClosersOnRemove(position)) {
                return this;
            }
            FormulaItem item = items.get(position);
            items.remove(position);
            item.setParent(null);
            removeAdded(item);
            invalidatePlaces();
        }
        return this;
    }

    //Is specified item first in formula
    public boolean isFirst(FormulaItem item) {
        if (items.isEmpty() && item == place) {
            return true;
        }
        return (items.indexOf(item) == 0);
    }

    //Is specified item last in formula
    public boolean isLast(FormulaItem item) {
        if (items.isEmpty() && item == place) {
            return true;
        }
        if (items.indexOf(item) < 0) {
            return false;
        }
        return (items.indexOf(item) == items.size() - 1);
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
        return items.get(index).getMovementFirst();
    }

    public Cursor getYourRight(FormulaItem item) {
        int index = items.indexOf(item) + 1;
        if (index >= items.size()) {
            return null;
        }
        return items.get(index).getMovementFirst();
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
        if (items.isEmpty() && position == 0) {
            return place;
        }
        return (position < 0 || position >= items.size()) ? null : items.get(position);
    }

    public int getItemPosition(FormulaItem item) {
        if (item == place) {
            return 0;
        }
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
        return getFirstItem().getMovementFirst();
    }

    public Cursor getLast() {
        return getLastItem().getLast();
    }

    public FormulaItem findLeftCloser(FormulaItem item) {
        int posTo = items.indexOf(item);
        if (posTo < 0) {
            return null;
        }

        int closers = 0;
        for (int pos = posTo; pos >= 0; pos--) {
            FormulaItem posItem = items.get(pos);
            if (posItem instanceof LeftCloser) {
                closers--;
                if (closers <= 0) {
                    return posItem;
                }
            } else if (posItem instanceof RightCloser) {
                closers++;
            }
        }

        return null;
    }

    public FormulaItem findRightCloser(FormulaItem item) {
        int posFrom = items.indexOf(item);
        if (posFrom < 0) {
            return null;
        }

        int size = items.size();
        int closers = 0;
        for (int pos = posFrom; pos < size; pos++) {
            FormulaItem posItem = items.get(pos);
            if (posItem instanceof LeftCloser) {
                closers++;
            } else if (posItem instanceof RightCloser) {
                closers--;
                if (closers <= 0) {
                    return posItem;
                }
            }
        }

        return null;
    }

    /*
     * Find the position of a LeftCloser that corresponds to the RightCloser in a specified position.
     */
    public int findLeftCloserPos(int posTo) {
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
    public int findRightCloserPos(int posFrom) {
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

    /*
     * Find maximum heightUp and heightDown of items in a specified part of a Formula
     */
    public Metrics findMaxHeights(Drawer drawer, int size, int posFrom, int count) {
        int posTo = Math.min(items.size(), posFrom + count);
        posFrom = Math.max(0, posFrom);
        int maxHeightUp = 0;
        int maxHeightDown = 0;
        for (int i = posFrom; i < posTo; i++) {
            Metrics itemMetrics = items.get(i).measure(drawer, size);
            maxHeightUp = Math.max(maxHeightUp, itemMetrics.getHeightUp());
            maxHeightDown = Math.max(maxHeightDown, itemMetrics.getHeightDown());
        }

        return new Metrics(0, maxHeightUp, maxHeightDown);
    }

    /*
     * Move specified formula part to another formula
     */
    public void moveFormula(Formula source, int posFrom, int size, Formula dest, int posTo) {
        for (int i = 0; i < size; i++) {
            FormulaItem item = source.getItem(posFrom);
            if (item == null) {
                return;
            }
            source.remove(item);
            dest.insertAt(posTo, item);
            posTo++;
        }
    }

    /*
     * Move specified formula part to another formula
     */
    public void moveFormula(int posFrom, int size, Formula dest, int posTo) {
        for (int i = 0; i < size; i++) {
            FormulaItem item = getItem(posFrom);
            if (item == null) {
                return;
            }
            remove(item);
            dest.insertAt(posTo, item);
            posTo++;
        }
    }
}
