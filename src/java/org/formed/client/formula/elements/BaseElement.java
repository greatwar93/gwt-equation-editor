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
public abstract class BaseElement implements FormulaItem {

    protected Formula parent = null;
    protected String val = "";
    protected int storedSize = 0;
    protected int storedX = 0;
    protected int storedY = 0;

    public BaseElement() {
    }

    public Formula getParent() {
        return parent;
    }

    public void setParent(Formula parent) {
        this.parent = parent;
    }

    public Metrics draw(FormulaDrawer drawer, int x, int y, int size) {
        storedSize = size;
        storedX = x;
        storedY = y;

        drawer.drawText(val, size, x, y);

        Metrics metrics = drawer.textMetrics(val, size);

        drawer.addDrawnItem(this, x, y, metrics);

        return metrics;
    }

    public Metrics measure(FormulaDrawer drawer, int size) {
        storedSize = size;

        return drawer.textMetrics(val, size);
    }

    protected int getLength() {
        return val.length();
/*        int p = 0;
        boolean escaped = false;
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            if (escaped) {
                if (c == ';') {
                    escaped = false;
                }
            } else {
                p++;
                if (c == '&') {
                    escaped = true;
                }
            }
        }

        return p;*/
    }

    private String getPart(int position) {
        return val.substring(0, position);
        /*
        StringBuilder s = new StringBuilder();
        int p = 0;
        boolean escaped = false;
        for (int i = 0; i < val.length() && p < position; i++) {
            char c = val.charAt(i);
            if (escaped) {
                s.append(c);
                if (c == ';') {
                    p++;
                    escaped = false;
                }
            } else {
                s.append(c);
                if (c == '&') {
                    escaped = true;
                } else {
                    p++;
                }
            }
        }

        return s.toString();*/
    }

    public Cursor getCursor(FormulaDrawer drawer, int x, int y) {
        int dx = x - storedX;

        int width = 0;
//        Metrics metrics = drawer.textMetrics(val.substring(0, 1), storedSize);
        Metrics metrics = drawer.textMetrics(getPart(1), storedSize);
        for (int i = 1; i <= getLength(); i++) {
            Metrics newMetrics = drawer.textMetrics(getPart(i), storedSize);
//            Metrics newMetrics = drawer.textMetrics(val.substring(0, i), storedSize);
            int newWidth = newMetrics.getWidth();

            if (newWidth > dx) {
                if ((newWidth - dx) > (newWidth - width) / 2) {
                    return new Cursor(drawer, this, i - 1, storedX + width, storedY, metrics.getHeightUp(), metrics.getHeightDown());
                } else {
                    return new Cursor(drawer, this, i, storedX + newWidth, storedY, newMetrics.getHeightUp(), newMetrics.getHeightDown());
                }
            }

            width = newWidth;
            metrics = newMetrics;
        }

        Metrics zeroMetrics = drawer.textMetrics("0", storedSize);
        return new Cursor(drawer, this, 0, storedX + width, storedY, zeroMetrics.getHeightUp(), zeroMetrics.getHeightDown());
    }

    public Cursor getLeft(FormulaDrawer drawer, int oldPosition) {
        int position = oldPosition - 1;

        if (position == 0) {
            if (parent != null) {
                if (!parent.isFirst(this)) {
                    return parent.getLeft(drawer, this);
                }
            }
        } else if (position < 0) {
            if (parent == null) {
                return null;
            }

            return parent.getLeft(drawer, this);
        }

        return getCursor(drawer, position);
    }

    public Cursor getRight(FormulaDrawer drawer, int oldPosition) {
        int position = oldPosition + 1;
        if (position > getLength()) {
            if (parent == null) {
                return null;
            }

            return parent.getRight(drawer, this);
        }

        return getCursor(drawer, position);
    }

    public Cursor getUp(FormulaDrawer drawer, int oldPosition) {
        if (parent == null) {
            return null;
        }
        return parent.getUp(drawer, this);
    }

    public Cursor getDown(FormulaDrawer drawer, int oldPosition) {
        if (parent == null) {
            return null;
        }
        return parent.getDown(drawer, this);
    }

    public Cursor getCursor(FormulaDrawer drawer, int position) {
        Metrics newMetrics = drawer.textMetrics(getPart(position), storedSize);
//        Metrics newMetrics = drawer.textMetrics(val.substring(0, position), storedSize);
        if (newMetrics.getHeight() == 0) {
            Metrics zeroMetrics = drawer.textMetrics("0", storedSize);
            newMetrics.setHeightUp(zeroMetrics.getHeightUp());
            newMetrics.setHeightDown(zeroMetrics.getHeightDown());
        }
        return new Cursor(drawer, this, position, storedX + newMetrics.getWidth(), storedY, newMetrics.getHeightUp(), newMetrics.getHeightDown());
    }

    public Cursor getFirst(FormulaDrawer drawer) {
        if (getLength() > 0 && parent != null) {
            if (!parent.isFirst(this)) {
                return getCursor(drawer, 1);
            }
        }
        return getCursor(drawer, 0);
    }

    public Cursor getLast(FormulaDrawer drawer) {
        return getCursor(drawer, getLength());
    }

    public Cursor childAsksLeft(FormulaDrawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getLeft(drawer, this);
    }

    public Cursor childAsksRight(FormulaDrawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getRight(drawer, this);
    }

    public Cursor childAsksUp(FormulaDrawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getUp(drawer, this);
    }

    public Cursor childAsksDown(FormulaDrawer drawer, Formula child) {
        if (parent == null) {
            return null;
        }
        return parent.getDown(drawer, this);
    }

    public void invalidateMetrics(Formula child) {
        if (parent != null) {
            parent.invalidateMetrics(this);
        }
    }

    public Cursor insertChar(FormulaDrawer drawer, Cursor cursor, char c) {
        if (parent == null) {
            return null;
        }
        FormulaItem item = new SimpleElement("" + c);
        parent.insertAfter(item, this);
        return item.getLast(drawer);
    }
}