/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.formed.client;

/**
 *
 * @author bulats
 */
public class Operator extends FormulaItem {

    private String operator = "";

    public Operator(Formula parent, String operator) {
        super(parent);
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
