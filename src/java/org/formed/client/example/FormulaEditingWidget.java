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
package org.formed.client.example;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.formed.client.formula.editor.AutoCompletion;
import org.formed.client.formula.editor.Clipboard;
import org.formed.client.formula.Editor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.editor.Undoer;
import org.formed.client.formula.elements.FunctionElement;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.RootElement;
import org.formed.client.formula.elements.SimpleElement;
import org.formed.client.formula.impl.SimpleEditor;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class FormulaEditingWidget extends Composite {

    private final FormulaWidget formulaWidget;
    private final Editor editor;
    private static List<AutoCompletion> autoSimple = new ArrayList<AutoCompletion>();
    private static List<AutoCompletion> autoFunction = new ArrayList<AutoCompletion>();
    private static List<AutoCompletion> autoNew = new ArrayList<AutoCompletion>();

    static {
        autoSimple.add(new AutoCompletion("α", "alfa", "α", new SimpleElement("α"), false));
        autoSimple.add(new AutoCompletion("α", "alpha", "α", new SimpleElement("α"), false));
        autoSimple.add(new AutoCompletion("α", "альфа", "α", new SimpleElement("α"), false));
        autoSimple.add(new AutoCompletion("β", "beta", "β", new SimpleElement("β"), false));
        autoSimple.add(new AutoCompletion("β", "бета", "β", new SimpleElement("β"), false));
        autoSimple.add(new AutoCompletion("γ", "gamma", "γ", new SimpleElement("γ"), false));
        autoSimple.add(new AutoCompletion("γ", "гамма", "γ", new SimpleElement("γ"), false));
        autoSimple.add(new AutoCompletion("δ", "delta", "δ", new SimpleElement("δ"), false));
        autoSimple.add(new AutoCompletion("δ", "дельта", "δ", new SimpleElement("δ"), false));
        autoSimple.add(new AutoCompletion("π", "pi", "π", new SimpleElement("π"), false));
        autoSimple.add(new AutoCompletion("π", "пи", "π", new SimpleElement("π"), false));
        autoSimple.add(new AutoCompletion("∞", "infinity", "∞", new SimpleElement("∞"), false));
        autoSimple.add(new AutoCompletion("∞", "бесконечность", "∞", new SimpleElement("∞"), false));

        autoFunction.add(new AutoCompletion("arcsin", "arcsin", "arcsin", new FunctionElement("arcsin"), false));
        autoFunction.add(new AutoCompletion("sin", "sin", "sin", new FunctionElement("sin"), false));
        autoFunction.add(new AutoCompletion("cos", "cos", "cos", new FunctionElement("sin"), false));

        autoNew.add(new AutoCompletion("root", "root", "root", new RootElement(new Formula(true)), true));
        autoNew.add(new AutoCompletion("arcsin", "arcsin", "arcsin", new FunctionElement("arcsin"), true));
        autoNew.add(new AutoCompletion("sin", "sin", "sin", new FunctionElement("sin"), true));
        autoNew.add(new AutoCompletion("cos", "cos", "cos", new FunctionElement("sin"), true));
        autoNew.add(new AutoCompletion("≤", "lessorequal", "≤", new OperatorElement("≤"), true));
        autoNew.add(new AutoCompletion("≤", "меньшеилиравно", "≤", new OperatorElement("≤"), true));
        autoNew.add(new AutoCompletion("≥", "greaterorequal", "≥", new OperatorElement("≥"), true));
        autoNew.add(new AutoCompletion("≥", "большеилиравно", "≥", new OperatorElement("≥"), true));
    }

    public FormulaEditingWidget(Formula formula, int width, int height, Undoer undoer, Clipboard clipboard) {
        formulaWidget = new FormulaWidget(formula, width, height, undoer);
        initWidget(formulaWidget);
        
        editor = new SimpleEditor(formula, formulaWidget.getDrawer(), undoer, clipboard);
        editor.populateAutoNew(autoNew);
        editor.populateAutoSimple(autoSimple);
        editor.populateAutoFunction(autoFunction);
        
        formulaWidget.setEditor(editor);
    }

    public void setUndoer(Undoer undoer, Button undoButton, Button redoButton) {
        formulaWidget.setUndoer(undoer, undoButton, redoButton);
    }

    public void setFocus(boolean focus){
        formulaWidget.setFocus(focus);
    }

    public Editor getEditor() {
        return editor;
    }

    public void redraw(){
        editor.redraw();
    }
}
