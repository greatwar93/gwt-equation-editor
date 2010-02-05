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
 * Auto-completion description object
 * @author Bulat Sirazetdinov
 */
public final class AutoCompletion {

    private final String showText;
    private String findText;
    private final String replaceWithText;
    private final FormulaItem newItem;
    private final boolean forNew;

    /**
     * 
     * @param showText text to be shown in an auto-completion drop-down box
     * @param findText text to be found in entered text to auto-complete with this object
     * @param replaceWithText text that should be used to replace auto-completed text
     * @param newItem item clones of which should be used to auto-complete
     * @param forNew true if this auto-completion description object is for creating new items, false otherwise
     */
    public AutoCompletion(String showText, String findText, String replaceWithText, FormulaItem newItem, boolean forNew) {
        this.showText = showText;
        this.findText = findText;
        this.replaceWithText = replaceWithText;
        this.newItem = newItem;
        this.forNew = forNew;
    }

    /**
     * Check whether this auto-completion description object is for creating new items, ot it is for editing items
     * @return true if this auto-completion description object is for creating new items, false otherwise
     */
    public boolean isForNew() {
        return forNew;
    }

    /**
     * Return text to be found in entered text to auto-complete with this object
     * @return text to be found in entered text to auto-complete with this object
     */
    public String getFindText() {
        return findText;
    }

    /**
     * Sets text to be found in entered text to auto-complete with this object
     * @param findText text to be found in entered text to auto-complete with this object
     */
    public void setFindText(String findText) {
        this.findText = findText;
    }

    /**
     * Returns item clones of which should be used to auto-complete
     * @return item clones of which should be used to auto-complete
     */
    public FormulaItem getNewItem() {
        return newItem;
    }

    /**
     * Return text that should be used to replace auto-completed text
     * @return text that should be used to replace auto-completed text
     */
    public String getReplaceWithText() {
        return replaceWithText;
    }

    /**
     * Returns text to be shown in an auto-completion drop-down box
     * @return text to be shown in an auto-completion drop-down box
     */
    public String getShowText() {
        return showText;
    }

    /**
     * Make a clone of this auto-completion description object
     * @return a newly created clone of this auto-completion description object
     */
    public AutoCompletion makeClone() {
        return new AutoCompletion(showText, findText, replaceWithText, newItem, forNew);
    }

    /**
     * Match specified text tail with the findText head.
     * Tail of an entered text should match the head of an auto-completed text.
     * @param text text to be matched with the findText
     * @return portion of findText head that matches specified text tail
     */
    public String match(String text) {
        int size = Math.min(findText.length(), text.length());
        int iText = text.length() - size;
        for (int i = size; i > 0; i--) {
            String find = findText.substring(0, i); //findText - from the head
            if (find.equals(text.substring(iText))) { //text - from the tail
                return find;
            }
            iText++;
        }
        return "";
    }
}
