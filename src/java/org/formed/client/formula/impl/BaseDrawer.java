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
package org.formed.client.formula.impl;

import java.util.HashMap;
import java.util.Map;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Editor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.drawer.Metrics;
import org.formed.client.formula.drawer.Rectangle;
import org.formed.client.formula.editor.AutoCompletion;

/**
 *
 * @author Bulat Sirazetdinov
 */
public abstract class BaseDrawer implements Drawer {

    protected Editor editor = Editor.ZERO_EDITOR;
    protected int debugTexts = 0;
    protected final Map<FormulaItem, Rectangle> items = new HashMap<FormulaItem, Rectangle>();
    protected final Map<Formula, Rectangle> formulas = new HashMap<Formula, Rectangle>();
    protected final Map<Integer, Rectangle> autoCompletions = new HashMap<Integer, Rectangle>();

    public BaseDrawer() {
    }

    public BaseDrawer(Editor editor) {
        this.editor = editor;
    }

    public void addDrawnItem(FormulaItem item, Rectangle rect) {
        items.put(item, rect);
    }

    public void addDrawnItem(FormulaItem item, int x, int y, Metrics metrics) {
        addDrawnItem(item, new Rectangle(x, y - metrics.getHeightUp(), metrics.getWidth(), metrics.getHeight()));
    }

    public void addDrawnFormula(Formula formula, Rectangle rect) {
        formulas.put(formula, rect);
    }

    public void addDrawnFormula(Formula formula, int x, int y, Metrics metrics) {
        addDrawnFormula(formula, new Rectangle(x, y - metrics.getHeightUp(), metrics.getWidth(), metrics.getHeight()));
    }

    public FormulaItem findItemAt(int x, int y) {
        Rectangle minRect = null;
        FormulaItem minRectItem = null;
        for (FormulaItem item : items.keySet()) {
            Rectangle rect = items.get(item);
            if (rect.isInside(x, y)) {
                if (rect.isSmaller(minRect)) {
                    minRectItem = item;
                    minRect = rect;
                }
            }
        }

        return minRectItem;
    }

    public void addDrawnAutoCompletion(int autoCompletionPosition, Rectangle rect) {
        autoCompletions.put(autoCompletionPosition, rect);
    }

    public void addDrawnAutoCompletion(int autoCompletionPosition, int x, int y, Metrics metrics) {
        addDrawnAutoCompletion(autoCompletionPosition, new Rectangle(x, y - metrics.getHeightUp(), metrics.getWidth(), metrics.getHeight()));
    }

    public int findAutoCompletionAt(int x, int y) {
        for (int item : autoCompletions.keySet()) {
            if(autoCompletions.get(item).isInside(x, y)) {
                return item;
            }
        }

        return -1;
    }

    public int sizeForHeight(String text, int height) {
        int sizeFrom = 1;
        int sizeTo = 4000;

        while (sizeFrom != sizeTo) {
            int size = (sizeTo + sizeFrom) / 2;
            Metrics metrics = measureText(text, size);
            if (height > metrics.getHeight()) {
                if (size == sizeFrom) {
                    return size;
                }
                sizeFrom = size;
            } else if (height < metrics.getHeight()) {
                if (size == sizeTo) {
                    return size;
                }
                sizeTo = size;
            } else {
                return size;
            }
        }

        return sizeFrom;
    }

    protected void preMeasure() {
        items.clear();
        formulas.clear();
        autoCompletions.clear();
    }

    protected void postMeasure(Metrics measuredMetrics) {
    }

    protected void preRedraw() {
        items.clear();
        formulas.clear();
        autoCompletions.clear();
    }

    protected void postRedraw(Metrics drawnMetrics) {
    }
}
