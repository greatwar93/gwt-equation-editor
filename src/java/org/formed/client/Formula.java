/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.formed.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bulats
 */
public class Formula {

    private final List<FormulaItem> items = new ArrayList<FormulaItem>();
    private Formula parent = null;

    public Formula() {
    }

    public Formula(Formula parent) {
        this.parent = parent;
    }

    public List<FormulaItem> getItems() {
        return items;
    }

    public Formula getParent() {
        return parent;
    }

}
