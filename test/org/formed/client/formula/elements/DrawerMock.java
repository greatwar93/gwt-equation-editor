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

import org.formed.client.formula.Formula;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.drawer.Metrics;
import org.formed.client.formula.drawer.Rectangle;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class DrawerMock implements Drawer {

    public void addDrawnItem(FormulaItem item, Rectangle rect) {
    }

    public void addDrawnItem(FormulaItem item, int x, int y, Metrics metrics) {
    }

    public void addDrawnFormula(Formula formula, Rectangle rect) {
    }

    public void addDrawnFormula(Formula formula, int x, int y, Metrics metrics) {
    }

    public Metrics measureText(String text, int size) {
        return new Metrics(text.length() * 10, 7, 7);
    }

    public void drawText(String text, int size, int x, int y) {
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
    }

    public int getSmallerSize(int size) {
        return size - 1;
    }

    public boolean moveCursorUp() {
        return true;
    }

    public boolean moveCursorDown() {
        return true;
    }

    public boolean moveCursorLeft() {
        return true;
    }

    public boolean moveCursorRight() {
        return true;
    }

    public FormulaItem selectItemAt(int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FormulaItem highlightItemAt(int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insert(char c) {
    }
}
