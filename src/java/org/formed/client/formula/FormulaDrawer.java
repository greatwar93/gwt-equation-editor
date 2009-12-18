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
package org.formed.client.formula;

import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;

/**
 *
 * @author bulats
 */
public interface FormulaDrawer {

    public enum Align {

        TOP, MIDDLE, BOTTOM
    }

    void addDrawnItem(FormulaItem item, Rectangle rect);
    void addDrawnItem(FormulaItem item, int x, int y, Metrics metrics);

    void addDrawnFormula(Formula formula, Rectangle rect);
    void addDrawnFormula(Formula formula, int x, int y, Metrics metrics);

    Metrics textMetrics(String text, int size);

    void drawText(String text, int size, int x, int y);

    int getSmallerSize(int size);

    boolean moveCursorUp();
    boolean moveCursorDown();
    boolean moveCursorLeft();
    boolean moveCursorRight();

    FormulaItem selectItemAt(int x, int y);
    FormulaItem highlightItemAt(int x, int y);

    CanvasPanelExt getCanvas();

    void redraw();
    void insert(char c);
}
