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

    Metrics draw(Drawer drawer, int x, int y, int size);

    Metrics measure(Drawer drawer, int size);

    //Get cursor for a mouse click
    Cursor getCursor(Drawer drawer, int x, int y);

    //Get cursor for a specified position
    Cursor getCursor(Drawer drawer, int position);

    //Move left
    Cursor getLeft(Drawer drawer, int oldPosition);

    //Move right
    Cursor getRight(Drawer drawer, int oldPosition);

    //Move up
    Cursor getUp(Drawer drawer, int oldPosition);

    //Move down
    Cursor getDown(Drawer drawer, int oldPosition);

    //Get position when come right from parent-formula
    Cursor getFirst(Drawer drawer);

    //Get position when come left from parent-formula
    Cursor getLast(Drawer drawer);

    //Get position when come left from child-formula
    Cursor childAsksLeft(Drawer drawer, Formula child);
    
    //Get position when come right from child-formula
    Cursor childAsksRight(Drawer drawer, Formula child);

    //Get position when come up from child-formula
    Cursor childAsksUp(Drawer drawer, Formula child);

    //Get position when come down from child-formula
    Cursor childAsksDown(Drawer drawer, Formula child);

    void reMeasureCursor(Drawer drawer, Cursor cursor);

    Cursor insertChar(Drawer drawer, Cursor cursor, char c);
    Cursor insertChar(Drawer drawer, int pos, char c);
    Cursor insertChar(Drawer drawer, int pos, FormulaItem item);
    Cursor removeChar(Drawer drawer, int pos);

    Command deleteLeft(Drawer drawer, Cursor cursor);
    Command deleteRight(Drawer drawer, Cursor cursor);

    void invalidatePlaces(Formula source);
    
    void invalidateMetrics();
}
