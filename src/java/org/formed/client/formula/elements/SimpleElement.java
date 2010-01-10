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

import org.formed.client.formula.Cursor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaDrawer;

/**
 *
 * @author bulats
 */
public final class SimpleElement extends PoweredElement {

    public SimpleElement(String name) {
        super();
        setName(name);
    }

    public SimpleElement(String name, Formula power) {
        super(power);
        setName(name);
    }

    public String getName() {
        return val;
    }

    public void setName(String name) {
        val = name;
    }

    @Override
    public Cursor insertChar(FormulaDrawer drawer, Cursor cursor, char c) {
        int pos = cursor.getPosition();
        val = val.substring(0, pos) + c + val.substring(pos);
        invalidateMetrics(null);
        return getCursor(drawer, pos + 1);
    }
}
