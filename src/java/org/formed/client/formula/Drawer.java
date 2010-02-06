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
import org.formed.client.formula.editor.AutoCompletion;

/**
 * Interface for objects used to draw formulas/items
 * @author Bulat Sirazetdinov
 */
public interface Drawer {

    /**
     * drawing alignment type:
     *    baseline is TOP
     *    baseline is MIDDLE
     *    baseline is BOTTOM
     */
    public enum Align {

        TOP, MIDDLE, BOTTOM
    }

    /**
     * Called by FormulaItem to register it's position on the screen
     * @param item FormulaItem object that is registering it's position
     * @param rect position of an object
     */
    void addDrawnItem(FormulaItem item, Rectangle rect);

    /**
     * Called by FormulaItem to register it's position on the screen
     * @param item FormulaItem object that is registering it's position
     * @param x x coordinate of FormulaItem baseline beginning
     * @param y y coordinate of FormulaItem baseline
     * @param metrics metrics of FormulaItem
     */
    void addDrawnItem(FormulaItem item, int x, int y, Metrics metrics);

    /**
     * Called by Formula to register it's position on the screen
     * @param formula Formula object that is registering it's position
     * @param rect position of an object
     */
    void addDrawnFormula(Formula formula, Rectangle rect);

    /**
     * Called by Formula to register it's position on the screen
     * @param formula Formula object that is registering it's position
     * @param x x coordinate of Formula baseline beginning
     * @param y y coordinate of Formula baseline
     * @param metrics metrics of Formula
     */
    void addDrawnFormula(Formula formula, int x, int y, Metrics metrics);

    FormulaItem findItemAt(int x, int y);

    void addDrawnAutoCompletion(int autoCompletionPosition, Rectangle rect);

    void addDrawnAutoCompletion(int autoCompletionPosition, int x, int y, Metrics metrics);

    int findAutoCompletionAt(int x, int y);

    /**
     * Calculate size of a text one step smaller
     * @param size size used to draw the item
     * @return calculated size
     */
    int getSmallerSize(int size);

    /**
     * Calculate text metrics
     * @param text text to calculate metrics for
     * @param size size of a font used to draw text
     * @return calculated metrics
     */
    Metrics measureText(String text, int size);

    int sizeForHeight(String text, int height);

    /**
     * Draw specified text at a specified point
     * @param text text to be drawn
     * @param size size of a font to use to draw text
     * @param x x coordinate of the beginning of a baseline
     * @param y y coordinate of the baseline
     */
    void drawText(String text, int size, int x, int y);

    /**
     * Draw line
     * @param x1 x coordinate of a first point
     * @param y1 y coordinate of a first point
     * @param x2 x coordinate of a second point
     * @param y2 y coordinate of a second point
     */
    void drawLine(int x1, int y1, int x2, int y2);

    void drawDottedLine(int x1, int y1, int x2, int y2);

    /**
     * Draw filled rectangle with a specified color
     * @param x1 x coordinate of a top-left corner
     * @param y1 y coordinate of a top-left corner
     * @param x2 x coordinate of a bottom-right corner
     * @param y2 y coordinate of a bottom-right corner
     * @param r red component (from 0 to 255)
     * @param g green component (from 0 to 255)
     * @param b blue component (from 0 to 255)
     */
    void fillRect(int x1, int y1, int x2, int y2, int r, int g, int b);

    /**
     * Draw rectangle
     * @param x1 x coordinate of a top-left corner
     * @param y1 y coordinate of a top-left corner
     * @param x2 x coordinate of a bottom-right corner
     * @param y2 y coordinate of a bottom-right corner
     */
    void drawRect(int x1, int y1, int x2, int y2);

    Metrics measure(Formula formula);

    Metrics redraw(Formula formula);

    int getWidth();

    int getHeight();

    boolean setWidth(int width);

    boolean setHeight(int height);
}
