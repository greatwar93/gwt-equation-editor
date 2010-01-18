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

import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaItem;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class RightCloser extends PoweredElement {

    public RightCloser() {
        val = ")";
    }

    public RightCloser(Formula power) {
        super(power);
        val = ")";
    }

    public FormulaItem makeClone() {
        RightCloser clone = new RightCloser(getPower().makeClone());
        clone.setParent(parent);

        return clone;
    }
}
