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

/**
 *
 * @author bulats
 */
public interface FormulaItem {

    Formula getParent();

    void setParent(Formula parent);

    boolean isComplex();

    Metrics draw(FormulaDrawer drawer, int x, int y, int size);

    Metrics measure(FormulaDrawer drawer, int size);

    //Get cursor for a mouse click
    CursorPosition getCursor(FormulaDrawer drawer, int x, int y);

    //Get cursor for a specified position
    CursorPosition getCursor(FormulaDrawer drawer, int position);

    //Move left
    CursorPosition getLeft(FormulaDrawer drawer, int oldPosition);

    //Move right
    CursorPosition getRight(FormulaDrawer drawer, int oldPosition);

    //Move up
    CursorPosition getUp(FormulaDrawer drawer, int oldPosition);

    //Move down
    CursorPosition getDown(FormulaDrawer drawer, int oldPosition);

    //Get position when come right from parent-formula
    CursorPosition getFirst(FormulaDrawer drawer);

    //Get position when come left from parent-formula
    CursorPosition getLast(FormulaDrawer drawer);

    //Get position when come left from child-formula
    CursorPosition childAsksLeft(FormulaDrawer drawer, Formula child);
    
    //Get position when come right from child-formula
    CursorPosition childAsksRight(FormulaDrawer drawer, Formula child);

    //Get position when come up from child-formula
    CursorPosition childAsksUp(FormulaDrawer drawer, Formula child);

    //Get position when come down from child-formula
    CursorPosition childAsksDown(FormulaDrawer drawer, Formula child);

    CursorPosition insertChar(FormulaDrawer drawer, CursorPosition cursor, char c);

    void invalidateMetrics(Formula child);
}
