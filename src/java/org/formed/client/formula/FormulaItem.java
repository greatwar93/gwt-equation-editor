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

    Formula getParent();

    FormulaItem makeClone();

    void setParent(Formula parent);

    boolean isComplex();

    //Draw this item on a specified drawer in a specified position of a specified size
    Metrics draw(Drawer drawer, int x, int y, int size);

    //Measure sizes of this item on a specified drawer of a specified size
    Metrics measure(Drawer drawer, int size);

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

    Cursor insertChar(Cursor cursor, char c);
    Cursor insertChar(int pos, char c);
    Cursor insertChar(int pos, FormulaItem item);
    Cursor removeChar(int pos);

    Command makeDeleteLeft(Cursor cursor);
    Command makeDeleteRight(Cursor cursor);

    void invalidatePlaces(Formula source);
    
    void invalidateMetrics();

    //Set item to be stroked through
    void setStrokeThrough(boolean strokeThrough);

    //Highlight an item with a background of a specified color
    void setHighlight(int r, int g, int b);

    //Switch highlighting of an item off
    void highlightOff();
}
