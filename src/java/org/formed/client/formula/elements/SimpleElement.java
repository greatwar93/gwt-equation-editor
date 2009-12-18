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

import org.formed.client.formula.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bulats
 */
public final class SimpleElement extends PoweredElement {

    private final static Map<String, String> names = new HashMap<String, String>();

    static {
        names.put("alpha", "&alpha;");
    }
    private String name;

    public SimpleElement(String name) {
        super();
        setName(name);
    }

    public SimpleElement(String name, Formula power) {
        super(power);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        val = names.get(name);
        if (val == null) {
            val = name;
        }
    }

    @Override
    public CursorPosition insertChar(FormulaDrawer drawer, CursorPosition cursor, char c) {
        int pos = cursor.getPosition();
        val = val.substring(0, pos) + c + val.substring(pos);
        name = val;
        invalidateMetrics(null);
        return getCursor(drawer, pos + 1);
    }
}
