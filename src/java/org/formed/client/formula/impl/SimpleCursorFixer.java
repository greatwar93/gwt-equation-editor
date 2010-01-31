/*
Copyright 2010 Bulat Sirazetdinov

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

import java.util.ArrayList;
import java.util.List;
import org.formed.client.formula.Cursor;
import org.formed.client.formula.CursorFixer;
import org.formed.client.formula.FormulaItem;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class SimpleCursorFixer implements CursorFixer {

    private final List<Cursor> cursors = new ArrayList<Cursor>();

    public void addCursor(Cursor cursor) {
        if (cursor != null) {
            cursors.add(cursor);
        }
    }

    public void clear() {
        cursors.clear();
    }

    public void removed(FormulaItem removedItem, Cursor newCursor) {
        for (Cursor cursor : cursors) {
            if (removedItem.isYouOrInsideYou(cursor.getItem())) {
                cursor.setCursor(newCursor);
            }
        }
    }
}
