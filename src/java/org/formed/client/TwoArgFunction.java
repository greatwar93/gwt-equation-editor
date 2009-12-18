/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.formed.client;

/**
 *
 * @author bulats
 */
public class TwoArgFunction extends FormulaItem {

    private String function = "";
    private final Formula formulaOne;
    private final Formula formulaTwo;

    public TwoArgFunction(Formula parent, String function) {
        super(parent);
        this.function = function;
        formulaOne = new Formula(parent);
        formulaTwo = new Formula(parent);
    }

    public Formula getFormulaOne() {
        return formulaOne;
    }

    public Formula getFormulaTwo() {
        return formulaTwo;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

}
