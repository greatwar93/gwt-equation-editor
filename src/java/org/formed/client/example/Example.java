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

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.formed.client.formula.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import org.formed.client.formula.impl.SurfaceDrawer;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import org.formed.client.formula.elements.SimpleElement;
import org.formed.client.formula.elements.RootElement;
import org.formed.client.formula.elements.FunctionElement;
import org.formed.client.formula.elements.OperatorElement;
import org.formed.client.formula.elements.DivisorElement;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.g2d.client.graphics.Surface;
import java.util.ArrayList;
import java.util.List;
import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;
import org.formed.client.formula.impl.SimpleUndoer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class Example {

    boolean arrow = false;

    public void draw() {

        //Create and fill a formula
        final Formula formula = new Formula();
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

        List<AutoCompletion> autoSimple = new ArrayList<AutoCompletion>();
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

        List<AutoCompletion> autoFunction = new ArrayList<AutoCompletion>();
        autoFunction.add(new AutoCompletion("arcsin", "arcsin", "arcsin", new FunctionElement("arcsin"), false));
        autoFunction.add(new AutoCompletion("sin", "sin", "sin", new FunctionElement("sin"), false));
        autoFunction.add(new AutoCompletion("cos", "cos", "cos", new FunctionElement("sin"), false));

        List<AutoCompletion> autoNew = new ArrayList<AutoCompletion>();
        autoNew.add(new AutoCompletion("root", "root", "root", new RootElement(new Formula(true)), true));
        autoNew.add(new AutoCompletion("arcsin", "arcsin", "arcsin", new FunctionElement("arcsin"), true));
        autoNew.add(new AutoCompletion("sin", "sin", "sin", new FunctionElement("sin"), true));
        autoNew.add(new AutoCompletion("cos", "cos", "cos", new FunctionElement("sin"), true));
        autoNew.add(new AutoCompletion("≤", "lessorequal", "≤", new OperatorElement("≤"), true));
        autoNew.add(new AutoCompletion("≤", "меньшеилиравно", "≤", new OperatorElement("≤"), true));
        autoNew.add(new AutoCompletion("≥", "greaterorequal", "≥", new OperatorElement("≥"), true));
        autoNew.add(new AutoCompletion("≥", "большеилиравно", "≥", new OperatorElement("≥"), true));

        //Use SimpleUndoer as an Undoer
        final SimpleUndoer undoer = new SimpleUndoer();


        //Create Surface and Drawer
        final int WIDTH = 630;
        final int HEIGHT = 150;
//        final CanvasPanelExt canvas1 = new CanvasPanelExt(WIDTH, HEIGHT);
//        canvas1.setCoordSize(WIDTH, HEIGHT);
        final Surface surface = new Surface(WIDTH, HEIGHT);
        final SurfaceDrawer drawer = new SurfaceDrawer(surface, undoer, formula);

        drawer.populateAutoNew(autoNew);
        drawer.populateAutoSimple(autoSimple);
        drawer.populateAutoFunction(autoFunction);

        //        RootPanel.get().add(canvas1, 10, 10);
        RootPanel.get().add(surface, 10, 10);
        /*FocusPanel panel = new FocusPanel();
        panel.setSize(Integer.toString(WIDTH), Integer.toString(HEIGHT));
        RootPanel.get().add(panel, 10, 10);*/


        //Debugging keylogger screen
        final HTML keys = new HTML("", true);
        keys.setWidth("500px");
        RootPanel.get().add(keys, 10, HEIGHT * 3 + 10);


        //Editor undo and redo buttons
        final Button undoButton = new Button("Undo");
        final Button redoButton = new Button("Redo");

        undoButton.setEnabled(false);
        undoButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                undoer.undo();
                drawer.redraw();
                surface.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        redoButton.setEnabled(false);
        redoButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                undoer.redo();
                drawer.redraw();
                surface.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        RootPanel.get().add(undoButton, 10, HEIGHT + 20);
        RootPanel.get().add(redoButton, undoButton.getOffsetWidth() + 20, HEIGHT + 20);


        //Editor cut, copy and paste buttons
        final Button cutButton = new Button("Cut");
        final Button copyButton = new Button("Copy");
        final Button pasteButton = new Button("Paste");

        cutButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.cut();
                surface.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        copyButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.copy();
                surface.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        pasteButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.paste();
                surface.setFocus(true);

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        RootPanel.get().add(cutButton, redoButton.getAbsoluteLeft() + redoButton.getOffsetWidth() + 30, HEIGHT + 20);
        RootPanel.get().add(copyButton, cutButton.getAbsoluteLeft() + cutButton.getOffsetWidth() + 10, HEIGHT + 20);
        RootPanel.get().add(pasteButton, copyButton.getAbsoluteLeft() + copyButton.getOffsetWidth() + 10, HEIGHT + 20);


        //Editor keyboard handlers
        surface.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                final int keycode = event.getNativeKeyCode();
                arrow = true;

                if (event.isLeftArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Left arrow - move selecting cursor left
                        keys.setHTML(keys.getHTML() + " Shift-Left");
                        drawer.selectLeft();
                    } else {
                        //Left arrow - move cursor left
                        keys.setHTML(keys.getHTML() + " Left");
                        drawer.moveCursorLeft();
                    }
                } else if (event.isRightArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Rigth arrow - move selecting cursor right
                        keys.setHTML(keys.getHTML() + " Shift-Right");
                        drawer.selectRight();
                    } else {
                        //Rigth arrow - move cursor right
                        keys.setHTML(keys.getHTML() + " Right");
                        drawer.moveCursorRight();
                    }
                } else if (event.isUpArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Up arrow - move selecting cursor up
                        keys.setHTML(keys.getHTML() + " Shift-Up");
                        drawer.selectUp();
                    } else {
                        //Up arrow
                        keys.setHTML(keys.getHTML() + " Up");
                        if (drawer.isAutoCompletion()) {
                            //move cursor up
                            drawer.moveAutoCompletionUp();
                        } else {
                            //move cursor up
                            drawer.moveCursorUp();
                        }
                    }
                } else if (event.isDownArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Down arrow - move selecting cursor down
                        keys.setHTML(keys.getHTML() + " Shift-Down");
                        drawer.selectDown();
                    } else {
                        //Down arrow
                        keys.setHTML(keys.getHTML() + " Down");
                        if (drawer.isAutoCompletion()) {
                            //move cursor down
                            drawer.moveAutoCompletionDown();
                        } else {
                            //move cursor down
                            drawer.moveCursorDown();
                        }
                    }

                } else if (keycode == KeyCodes.KEY_ENTER) {
                    //Enter - auto-complete
                    keys.setHTML(keys.getHTML() + " Enter");
                    if (drawer.isAutoCompletion()) {
                        drawer.selectAutoCompletion();
                    }
                } else if (keycode == KeyCodes.KEY_ESCAPE) {
                    //Esc - hide auto-complete
                    keys.setHTML(keys.getHTML() + " Esc");
                    if (drawer.isAutoCompletion()) {
                        drawer.hideAutoCompletion();
                    }
                } else if (keycode == 32) {
                    if (event.isControlKeyDown()) {
                        //Ctrl-Space - show auto-complete
                        keys.setHTML(keys.getHTML() + " Ctrl-Space");
                        drawer.showAutoCompletion();
                    } else {
                        //Space - move cursor right
                        keys.setHTML(keys.getHTML() + " Space");
                        drawer.moveCursorRight();
                    }

                } else if (keycode == KeyCodes.KEY_DELETE) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Del - cut selected
                        keys.setHTML(keys.getHTML() + " Shift-Del");
                        drawer.cut();
                    } else {
                        //Del - delete to the right from cursor
                        keys.setHTML(keys.getHTML() + " Del");
                        drawer.deleteRight();
                    }
                } else if (keycode == KeyCodes.KEY_BACKSPACE) {
                    if (event.isControlKeyDown()) {
                        //Ctrl-Backspace - undo
                        keys.setHTML(keys.getHTML() + " Ctrl-Backspace");
                        undoer.undo();
                        drawer.redraw();
                    } else {
                        //Backspace - delete from the left from cursor
                        keys.setHTML(keys.getHTML() + " Backspace");
                        drawer.deleteLeft();
                    }

                } else if (event.isControlKeyDown() && keycode == 88) {
                    //Ctrl-X - cut selected
                    keys.setHTML(keys.getHTML() + " Ctrl-X");
                    drawer.cut();
                } else if (event.isShiftKeyDown() && keycode == 45) {
                    //Shift-Ins - paste
                    keys.setHTML(keys.getHTML() + " Shift-Ins");
                    drawer.paste();
                } else if (event.isControlKeyDown() && keycode == 86) {
                    //Ctrl-V - paste
                    keys.setHTML(keys.getHTML() + " Ctrl-V");
                    drawer.paste();
                } else if (event.isControlKeyDown() && keycode == 45) {
                    //Ctrl-Ins - copy selected
                    keys.setHTML(keys.getHTML() + " Ctrl-Ins");
                    drawer.copy();
                } else if (event.isControlKeyDown() && keycode == 67) {
                    //Ctrl-C - copy selected
                    keys.setHTML(keys.getHTML() + " Ctrl-C");
                    drawer.copy();

                } else if (event.isControlKeyDown() && keycode == 90) {
                    //Ctrl-Z - undo
                    keys.setHTML(keys.getHTML() + " Ctrl-Z");
                    undoer.undo();
                    drawer.redraw();
                } else if (event.isControlKeyDown() && keycode == 89) {
                    //Ctrl-Y - redo
                    keys.setHTML(keys.getHTML() + " Ctrl-Y");
                    undoer.redo();
                    drawer.redraw();

                } else if (event.isControlKeyDown()) {
                    keys.setHTML(keys.getHTML() + "+p" + event.getNativeKeyCode());
                } else {
                    //keys.setHTML(keys.getHTML() + "+p" + event.getNativeKeyCode());
                    arrow = false;
                }

                if (arrow) {
                    event.preventDefault();
                }

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        surface.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (!arrow) { //Insert entered char
                    if (event.getCharCode() == '^') {
                        //^ - move cursor to power
                        keys.setHTML(keys.getHTML() + " '^'");
                        drawer.moveCursorToPower();
                    } else {
                    keys.setHTML(keys.getHTML() + " '"+event.getCharCode()+"'");
                        drawer.insert(event.getCharCode());
                    }
                } else { //skip event
                    arrow = false;
                }
                event.preventDefault();

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        surface.addKeyUpHandler(new KeyUpHandler() {

            public void onKeyUp(KeyUpEvent event) {
                arrow = false;
                event.preventDefault();
            }
        });


        //Editor mouse handlers
        surface.addMouseMoveHandler(new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
                //drawer.highlightItemAt(event.getX(), event.getY());
                drawer.mouseMoveAt(event.getX(), event.getY());
            }
        });

        surface.addMouseUpHandler(new MouseUpHandler() {

            public void onMouseUp(MouseUpEvent event) {
                //drawer.selectItemAt(event.getX(), event.getY());
                drawer.mouseUpAt(event.getX(), event.getY());
            }
        });

        surface.addMouseDownHandler(new MouseDownHandler() {

            public void onMouseDown(MouseDownEvent event) {
                //drawer.selectItemAt(event.getX(), event.getY());
                drawer.mouseDownAt(event.getX(), event.getY());
            }
        });

        //Insert special buttons
        final Button sinButton = new Button("sin", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("sin"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button cosButton = new Button("cos", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("cos"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button tgButton = new Button("tg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("tg"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button ctgButton = new Button("ctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("ctg"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arcsinButton = new Button("arcsin", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arcsin"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arccosButton = new Button("arccos", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arccos"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arctgButton = new Button("arctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arctg"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button arcctgButton = new Button("arcctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arcctg"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button rootButton = new Button("root", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new RootElement(new Formula()));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        final Button divideButton = new Button("÷", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new OperatorElement("÷"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button plusMinusButton = new Button("±", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new OperatorElement("±"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button lessOrEqualButton = new Button("≤", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new OperatorElement("≤"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button greaterOrEqualButton = new Button("≥", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new OperatorElement("≥"));
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button alphaButton = new Button("α", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insert('α');
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button piButton = new Button("π", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insert('π');
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button angleButton = new Button("∠", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insert('∠');
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button degreeButton = new Button("°", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insert('°');
                drawer.redraw();
                surface.setFocus(true);
                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });
        final Button infinityButton = new Button("∞", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insert('∞');
                drawer.redraw();
                surface.setFocus(true);
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

        RootPanel.get().add(sinButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 50);
        RootPanel.get().add(cosButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 50);
        RootPanel.get().add(tgButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 50);
        RootPanel.get().add(ctgButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 50);
        RootPanel.get().add(rootButton, 10 + (10 + BUTTON_WIDTH) * 4, HEIGHT + 50);

        RootPanel.get().add(arcsinButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 80);
        RootPanel.get().add(arccosButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 80);
        RootPanel.get().add(arctgButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 80);
        RootPanel.get().add(arcctgButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 80);

        RootPanel.get().add(divideButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 110);
        RootPanel.get().add(plusMinusButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 110);
        RootPanel.get().add(lessOrEqualButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 110);
        RootPanel.get().add(greaterOrEqualButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 110);

        RootPanel.get().add(alphaButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 140);
        RootPanel.get().add(piButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 140);
        RootPanel.get().add(angleButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 140);
        RootPanel.get().add(degreeButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 140);
        RootPanel.get().add(infinityButton, 10 + (10 + BUTTON_WIDTH) * 4, HEIGHT + 140);
        //Special buttons inserted


        //Initial update of the screen
        drawer.redraw();

    }
}
