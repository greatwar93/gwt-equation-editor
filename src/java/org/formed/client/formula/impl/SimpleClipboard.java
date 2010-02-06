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
import org.formed.client.formula.editor.Clipboard;
import org.formed.client.formula.FormulaItem;

/**
 * Simple clipboard handling
 * @author Bulat Sirazetdinov
 */
public class SimpleClipboard implements Clipboard{

    private final List<FormulaItem> items = new ArrayList<FormulaItem>();

    public void copy(List<FormulaItem> list) {
        items.clear();
        for(FormulaItem item : list){
            FormulaItem clone = item.makeClone();
            clone.setParent(null);
            items.add(clone);
        }
    }

    public List<FormulaItem> paste() {
        List<FormulaItem> list = new ArrayList<FormulaItem>();
        for(FormulaItem item : items){
            list.add(item.makeClone());
        }
        return list;
    }

}
