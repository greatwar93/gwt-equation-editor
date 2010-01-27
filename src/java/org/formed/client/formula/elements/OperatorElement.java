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
package org.formed.client.formula.elements;

import org.formed.client.formula.Cursor;
import org.formed.client.formula.FormulaItem;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class OperatorElement extends BaseElement {

    private String name;

    public OperatorElement(String name) {
        this.name = name;
        val = name;
    }

    public FormulaItem makeClone() {
        OperatorElement clone = new OperatorElement(name);
        clone.setParent(parent);
        
        return clone;
    }

    public boolean isComplex() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        val = name;
    }

    @Override
    public Cursor insertChar(Cursor cursor, char c) {
        if (parent == null) {
            return null;
        }
        FormulaItem item = new SimpleElement("" + c);
        if (cursor.getPosition() == 0) {
            parent.insertBefore(item, this);
        } else {
            parent.insertAfter(item, this);
        }
        return item.getLast();
    }
}
