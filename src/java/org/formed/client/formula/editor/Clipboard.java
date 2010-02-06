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
package org.formed.client.formula.editor;

import org.formed.client.formula.*;
import java.util.Collections;
import java.util.List;

/**
 * Interface used by clipboard handling objects
 * @author Bulat Sirazetdinov
 */
public interface Clipboard {

    /**
     * A value to be used as instead of null Clipboard
     */
    public static final Clipboard ZERO_CLIPBOARD = new Clipboard() {

        public void copy(List<FormulaItem> list) {

        }

        public List<FormulaItem> paste() {
            return Collections.emptyList();
        }
    };

    /**
     * Copy specified elements to clipboard (makes clones)
     * @param list list of elements to copy to clipboard
     */
    void copy(List<FormulaItem> list);

    /**
     * Paste elements from clipboard (makes clones)
     * @return list of clones of elements from clipboard
     */
    List<FormulaItem> paste();
}
