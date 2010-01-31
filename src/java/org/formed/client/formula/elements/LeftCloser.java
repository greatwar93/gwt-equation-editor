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
public final class LeftCloser extends BaseElement {

    public LeftCloser() {
        val = "(";
    }

    public FormulaItem makeClone() {
        LeftCloser clone = new LeftCloser();
        clone.setParent(parent);

        return clone;
    }

    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isLeftCloser() {
        return true;
    }

    @Override
    public boolean isIncorporatable() {
        return false;
    }

    public FormulaItem getRightCloser(){
        if(parent == null) return null;
        return parent.findRightCloser(this);
    }

    @Override
    public HowToInsert getHowToInsert(Cursor cursor, FormulaItem item) {
        if(item == null || cursor == null) return HowToInsert.NONE;

        return cursor.getPosition() <= 0 ? HowToInsert.LEFT : HowToInsert.RIGHT;
    }
}
