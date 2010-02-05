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
package org.formed.client.formula;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class AutoCompletion {

    private final String showText;
    private String findText;
    private final String replaceWithText;
    private final FormulaItem newItem;
    private final boolean forNew;

    public AutoCompletion(String showText, String findText, String replaceWithText, FormulaItem newItem, boolean forNew) {
        this.showText = showText;
        this.findText = findText;
        this.replaceWithText = replaceWithText;
        this.newItem = newItem;
        this.forNew = forNew;
    }

    public boolean isForNew() {
        return forNew;
    }

    public String getFindText() {
        return findText;
    }

    public void setFindText(String findText) {
        this.findText = findText;
    }

    public FormulaItem getNewItem() {
        return newItem;
    }

    public String getReplaceWithText() {
        return replaceWithText;
    }

    public String getShowText() {
        return showText;
    }

    public AutoCompletion makeClone() {
        return new AutoCompletion(showText, findText, replaceWithText, newItem, forNew);
    }

    public String match(String text) {
        int size = Math.min(findText.length(), text.length());
        int iText = text.length() - size;
        for (int i = size; i > 0; i--) {
            String find = findText.substring(0, i);
            if (find.equals(text.substring(iText))) {
                return find;
            }
            iText++;
        }
        return "";
    }
}
