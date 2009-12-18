/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.formed.client;

/**
 *
 * @author bulats
 */
public class Variable extends FormulaItem {

    private String variable = "";

    public Variable(Formula parent, String variable) {
        super(parent);
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
