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
 *
 * @author Bulat Sirazetdinov
 */
public interface FormulaItem {

    public enum HowToInsert {
        INSERT, LEFT, RIGHT, BREAK, NONE
    }

    FormulaItem makeClone();

    //Returns true if item has parent
    boolean hasParent();

    //Returns items parent
    Formula getParent();

    //Sets a new parent for an item and returns the former one
    Formula setParent(Formula parent);

    //Removes item from its parent, sets a parent to null and returns that former parent
    Formula removeFromParent();

    //Returns true if an item is of any type of a right closer
    boolean isRightCloser();

    //Returns true if an item is of any type of a left closer
    boolean isLeftCloser();

    //Returns true if an item can be incorporated
    boolean isIncorporatable();

    //Returns true if an item is a complex one
    boolean isComplex();

    //Draw this item on a specified drawer in a specified position of a specified size
    Metrics draw(Drawer drawer, int x, int y, int size);

    //Measure sizes of this item on a specified drawer of a specified size
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
    Cursor getFirst();

    //Get position when come left from parent-formula
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

    //Child-formula calls this when it's got empty
    //void childCollapsed(Formula child);
    void reMeasureCursor(Drawer drawer, Cursor cursor);

    //Set item to be stroked through
    void setStrokeThrough(boolean strokeThrough);

    //Highlight an item with a background of a specified color
    void setHighlight(int r, int g, int b);

    //Switch highlighting of an item off
    void highlightOff();

    //Get the insertion method for a specified item
    HowToInsert getHowToInsert(Cursor cursor, FormulaItem item);

    //Create Command to insert specified item into this item
    Command buildInsert(Cursor cursor, FormulaItem item);

    //Create Command to break this item with the specified item
    Command buildBreakWith(Cursor cursor, FormulaItem item);

    //Create Command to insert specified item before this item
    Command buildInsertBefore(FormulaItem item);

    //Create Command to insert specified item after this item
    Command buildInsertAfter(FormulaItem item);

    //Create Command to incorporate item from the left
    Command buildIncorporateLeft();

    //Create Command to incorporate item from the right
    Command buildIncorporateRight();

    //Create Command to delete something left to cursor
    Command buildDeleteLeft(Cursor cursor);

    //Create Command to delete something right to cursor
    Command buildDeleteRight(Cursor cursor);
}
