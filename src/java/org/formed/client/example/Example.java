/*
Copyright 2010 Bulat Sirazetdinov
Copyright 2009 Bulat Sirazetdinov

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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.formed.client.formula.elements.SimpleElement;
import org.formed.client.formula.elements.RootElement;
import org.formed.client.formula.elements.FunctionElement;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.DivisorElement;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;
import org.formed.client.formula.Formula;
import org.formed.client.formula.impl.SimpleClipboard;
import org.formed.client.formula.impl.SimpleUndoer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class Example {

    boolean arrow = false;

    public void draw() {

        //Create and fill a formula
        final Formula formula = new Formula(true);
        formula.add(new SimpleElement("S"));
        formula.add(new OperatorElement("="));
        formula.add(new SimpleElement("a", new Formula().add(new SimpleElement("2"))));
        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("α"))));
        formula.add(new OperatorElement("-"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a", true)), new Formula().add(new SimpleElement("a", true)).add(new OperatorElement("·")).add(new SimpleElement("b")).add(new OperatorElement("+")).add(new SimpleElement("a", true))));
        formula.add(new OperatorElement("·"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a")).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta"))));
        formula.add(new OperatorElement("+"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a", new Formula().add(new RootElement(new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("alpha")).add(new SimpleElement("b", new Formula().add(new SimpleElement("2")))))))).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new RootElement(new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new DivisorElement(new Formula().add(new SimpleElement("beta")), new Formula().add(new FunctionElement("sin", new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new SimpleElement("β")), new Formula().add(new SimpleElement("2"))))))))));
        formula.add(new OperatorElement("+"));
//        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("2"))));
//        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("α")), new Formula().add(new SimpleElement("e", new Formula().add(new SimpleElement("x"))))));


        //Use SimpleUndoer as an Undoer
        final SimpleUndoer undoer = new SimpleUndoer();

        //Create Surface and Drawer
        final int WIDTH = 630;
        final int HEIGHT = 150;
/*        final CanvasPanelExt canvas1 = new CanvasPanelExt(WIDTH, HEIGHT);
        canvas1.setCoordSize(WIDTH, HEIGHT);
        RootPanel.get().add(canvas1, 10, 10);
        FocusPanel panel = new FocusPanel();
        panel.setSize(Integer.toString(WIDTH), Integer.toString(HEIGHT));
        RootPanel.get().add(panel, 10, 10);*/

        final FormulaEditingWidget formulaEditingWidget = new FormulaEditingWidget(formula, WIDTH, HEIGHT, undoer, new SimpleClipboard());

        RootPanel.get().add(formulaEditingWidget, 10, 10);

        VerticalPanel panel = new VerticalPanel();
        RootPanel.get().add(panel, 10, 410);

        //Debugging keylogger screen
        panel.add(new HTML("Key log:"));
        final HTML keys = new HTML("", true);
        keys.setWidth("500px");
        panel.add(keys);

        //Editor undo and redo buttons
        final Button undoButton = new Button("Undo");
        final Button redoButton = new Button("Redo");
        formulaEditingWidget.setUndoer(undoer, undoButton, redoButton);

        undoButton.setEnabled(false);
        undoButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                undoer.undo();
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        redoButton.setEnabled(false);
        redoButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                undoer.redo();
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        RootPanel.get().add(undoButton, 10, HEIGHT + 70);
        RootPanel.get().add(redoButton, undoButton.getOffsetWidth() + 20, HEIGHT + 70);


        //Editor cut, copy and paste buttons
        final Button cutButton = new Button("Cut");
        final Button copyButton = new Button("Copy");
        final Button pasteButton = new Button("Paste");

        cutButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().cut();
                formulaEditingWidget.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        copyButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().copy();
                formulaEditingWidget.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        pasteButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().paste();
                formulaEditingWidget.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        RootPanel.get().add(cutButton, redoButton.getAbsoluteLeft() + redoButton.getOffsetWidth() + 30, HEIGHT + 70);
        RootPanel.get().add(copyButton, cutButton.getAbsoluteLeft() + cutButton.getOffsetWidth() + 10, HEIGHT + 70);
        RootPanel.get().add(pasteButton, copyButton.getAbsoluteLeft() + copyButton.getOffsetWidth() + 10, HEIGHT + 70);


        //Insert special buttons
        final Button sinButton = new Button("sin", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("sin"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button cosButton = new Button("cos", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("cos"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button tgButton = new Button("tg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("tg"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button ctgButton = new Button("ctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("ctg"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arcsinButton = new Button("arcsin", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("arcsin"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arccosButton = new Button("arccos", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("arccos"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arctgButton = new Button("arctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("arctg"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arcctgButton = new Button("arcctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new FunctionElement("arcctg"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button rootButton = new Button("root", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new RootElement(new Formula()));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button divideButton = new Button("÷", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new OperatorElement("÷"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button plusMinusButton = new Button("±", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new OperatorElement("±"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button lessOrEqualButton = new Button("≤", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new OperatorElement("≤"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button greaterOrEqualButton = new Button("≥", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insertElement(new OperatorElement("≥"));
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button alphaButton = new Button("α", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insert('α');
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button piButton = new Button("π", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insert('π');
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button angleButton = new Button("∠", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insert('∠');
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button degreeButton = new Button("°", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insert('°');
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button infinityButton = new Button("∞", new ClickHandler() {

            public void onClick(ClickEvent event) {
                formulaEditingWidget.getEditor().insert('∞');
                formulaEditingWidget.redraw();
                formulaEditingWidget.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final int BUTTON_WIDTH = 50;
        sinButton.setWidth(BUTTON_WIDTH + "px");
        cosButton.setWidth(BUTTON_WIDTH + "px");
        tgButton.setWidth(BUTTON_WIDTH + "px");
        ctgButton.setWidth(BUTTON_WIDTH + "px");
        rootButton.setWidth(BUTTON_WIDTH + "px");

        arcsinButton.setWidth(BUTTON_WIDTH + "px");
        arccosButton.setWidth(BUTTON_WIDTH + "px");
        arctgButton.setWidth(BUTTON_WIDTH + "px");
        arcctgButton.setWidth(BUTTON_WIDTH + "px");

        divideButton.setWidth(BUTTON_WIDTH + "px");
        plusMinusButton.setWidth(BUTTON_WIDTH + "px");
        lessOrEqualButton.setWidth(BUTTON_WIDTH + "px");
        greaterOrEqualButton.setWidth(BUTTON_WIDTH + "px");

        alphaButton.setWidth(BUTTON_WIDTH + "px");
        piButton.setWidth(BUTTON_WIDTH + "px");
        angleButton.setWidth(BUTTON_WIDTH + "px");
        degreeButton.setWidth(BUTTON_WIDTH + "px");
        infinityButton.setWidth(BUTTON_WIDTH + "px");

        RootPanel.get().add(sinButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 100);
        RootPanel.get().add(cosButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 100);
        RootPanel.get().add(tgButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 100);
        RootPanel.get().add(ctgButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 100);
        RootPanel.get().add(rootButton, 10 + (10 + BUTTON_WIDTH) * 4, HEIGHT + 100);

        RootPanel.get().add(arcsinButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 130);
        RootPanel.get().add(arccosButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 130);
        RootPanel.get().add(arctgButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 130);
        RootPanel.get().add(arcctgButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 130);

        RootPanel.get().add(divideButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 160);
        RootPanel.get().add(plusMinusButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 160);
        RootPanel.get().add(lessOrEqualButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 160);
        RootPanel.get().add(greaterOrEqualButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 160);

        RootPanel.get().add(alphaButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 190);
        RootPanel.get().add(piButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 190);
        RootPanel.get().add(angleButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 190);
        RootPanel.get().add(degreeButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 190);
        RootPanel.get().add(infinityButton, 10 + (10 + BUTTON_WIDTH) * 4, HEIGHT + 190);
        //Special buttons inserted


        //Initial update of the screen
        formulaEditingWidget.redraw();

    }
}
