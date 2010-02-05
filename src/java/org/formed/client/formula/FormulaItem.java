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

/**
 * Interface for all formula items/elements
 * @author Bulat Sirazetdinov
 */
public interface FormulaItem {

    /**
     * Enumeration of new item insertion methods
     */
    public enum HowToInsert {
        INSERT, LEFT, RIGHT, BREAK, NONE
    }

    /**
     * Create a clone of an item
     * @return newly created clone of an item
     */
    FormulaItem makeClone();

    /**
     * Check whether item has parent
     * @return true if item has parent, otherwise - false
     */
    boolean hasParent();

    /**
     * Get items parent
     * @return items parent, null - if item has no parent
     */
    Formula getParent();

    /**
     * Sets a new parent for an item
     * @param parent new parent for an item
     * @return former parent, null - if item had no parent
     */
    Formula setParent(Formula parent);

    /**
     * Removes item from its parent, sets a parent to null
     * @return former parent, null - if item had no parent
     */
    Formula removeFromParent();

    /**
     * Checks whether item is of any type of a right closer
     * @return true if an item is of any type of a right closer, false otherwise
     */
    boolean isRightCloser();

    /**
     * Checks whether item is of any type of a left closer
     * @return true if an item is of any type of a left closer, false otherwise
     */
    boolean isLeftCloser();

    /**
     * Check if an item can be automatically incorporated into another.
     * This method is used when adding new items, to check their neighbours before automatically incorporating them in a newly inserted item.
     * @return true if an item can be automatically incorporated, false otherwise
     */
    boolean isIncorporatable();

    /**
     * Check whether an item is empty
     * @return true if an item is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Check whether an item is a complex item
     * @return true if an item is a complex one, false otherwise
     */
    boolean isComplex();

    /**
     * Check whether specified item is this one or inside this.
     * @param item item to search for
     * @return true if specified item is this one or inside it, false otherwise
     */
    boolean isYouOrInsideYou(FormulaItem item);

    /**
     * Draw this item on a specified Drawer object in a specified position of a specified size
     * @param drawer Drawer object to use to draw
     * @param x x coordinate of items baseline beginning
     * @param y y coordinate of items baseline
     * @param size size of a font to use
     * @return metrics of a drawn item
     */
    Metrics draw(Drawer drawer, int x, int y, int size);

    /**
     * Measure sizes of this item on a specified Drawer of a specified size
     * @param drawer Drawer object to use to measure item
     * @param size size of a font to use
     * @return metrics of an item
     */
    Metrics measure(Drawer drawer, int size);

    void invalidatePlaces(Formula source);

    void invalidateMetrics();

    //Get cursor for a mouse click
    Cursor getCursor(Drawer drawer, int x, int y);

    //Get cursor for a specified position
    Cursor getCursor(int position);

    //Move left
    Cursor getLeft(int oldPosition);

    //Move right
    Cursor getRight(int oldPosition);

    //Move up
    Cursor getUp(int oldPosition);

    //Move down
    Cursor getDown(int oldPosition);

    //Get position when come right from parent-formula
    Cursor getMovementFirst();

    //Get first position
    Cursor getFirst();

    //Get last position
    Cursor getLast();

    //Get position when just inserted an item
    Cursor getEditPlace();

    //Get position when come left from child-formula
    Cursor childAsksLeft(Formula child);

    //Get position when come right from child-formula
    Cursor childAsksRight(Formula child);

    //Get position when come up from child-formula
    Cursor childAsksUp(Formula child);

    //Get position when come down from child-formula
    Cursor childAsksDown(Formula child);

    /**
     * Calculate cursor metrics (can be used only after item being drawn)
     * @param drawer Drawer to use to calculate cursor metrics
     * @param cursor Cursor object which metrics should be measured/re-measured
     */
    void measureCursor(Drawer drawer, Cursor cursor);

    //Set item to be stroked through
    void setStrokeThrough(boolean strokeThrough);

    //Highlight an item with a background of a specified color
    void setHighlight(int r, int g, int b);

    //Switch highlighting of an item off
    void highlightOff();

    /**
     * Get the insertion method for a specified item into this item
     * @param cursor Cursor object used to detect at which position to insert a new item
     * @param item a new item to be inserted
     * @return insertion method for a specified item
     */
    HowToInsert getHowToInsert(Cursor cursor, FormulaItem item);

    /**
     * Create Command to insert specified item into this item
     * @param cursor Cursor object used to detect at which position to insert a new item
     * @param item a new item to be inserted into this item
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildInsert(Cursor cursor, FormulaItem item, CursorFixer fixer);

    /**
     * Create Command to break this item with the specified item
     * @param cursor Cursor object used to detect at which position to break item
     * @param item a new item to be inserted between two chunks of this item
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildBreakWith(Cursor cursor, FormulaItem item, CursorFixer fixer);

    /**
     * Create Command to insert specified item before this item
     * @param item a new item to be inserted right before this item
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildInsertBefore(FormulaItem item, CursorFixer fixer);

    /**
     * Create Command to insert specified item after this item
     * @param item a new item to be inserted right after this item
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildInsertAfter(FormulaItem item, CursorFixer fixer);

    /**
     * Create Command to incorporate item from the left
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildIncorporateLeft(CursorFixer fixer);

    /**
     * Create Command to incorporate item from the right
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildIncorporateRight(CursorFixer fixer);

    /**
     * Create Command to delete something left to cursor
     * @param cursor Cursor object used to detect from which position to delete
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildDeleteLeft(Cursor cursor, CursorFixer fixer);

    /**
     * Create Command to delete something right to cursor
     * @param cursor Cursor object used to detect from which position to delete
     * @param fixer CursorFixer object to use to fix cursors when executing/undoing command (to move cursors away from deleted items)
     * @return created Command object on success, Command.ZERO_COMMAND otherwise
     */
    Command buildDeleteRight(Cursor cursor, CursorFixer fixer);
}
