/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.formed.client;

/**
 *
 * @author bulats
 */
public abstract class FormulaItem {
    private Formula parent = null;

    public FormulaItem() {
    }

    public FormulaItem(Formula parent) {
        this.parent = parent;
    }

    public Formula getParent() {
        return parent;
    }
}
