/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.formed.client;

/**
 *
 * @author bulats
 */
public class Function extends FormulaItem {

    private String function = "";
    private final Formula formula;

    public Function(Formula parent, String function) {
        super(parent);
        this.function = function;
        formula = new Formula(parent);
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Formula getFormula() {
        return formula;
    }

}
