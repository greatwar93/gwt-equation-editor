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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.g2d.client.graphics.Surface;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.Editor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.editor.Undoer;
import org.formed.client.formula.impl.SurfaceDrawer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class FormulaWidget extends Composite {

    private final Surface surface;
    private final Drawer drawer;
    private final Formula formula;
    private Editor editor = Editor.ZERO_EDITOR;
    private Undoer undoer = Undoer.ZERO_UNDOER;
    private Button undoButton = new Button();
    private Button redoButton = new Button();

    public FormulaWidget(Formula formula, int width, int height) {
        this.formula = formula;
        surface = new Surface(width, height);
        initWidget(surface);
        drawer = new SurfaceDrawer(surface);

        initKeyboadrHandling();
        initMouseHandling();
    }

    public FormulaWidget(Formula formula, int width, int height, Undoer undoer) {
        this.formula = formula;
        surface = new Surface(width, height);
        initWidget(surface);
        drawer = new SurfaceDrawer(surface);
        this.undoer = undoer;

        initKeyboadrHandling();
        initMouseHandling();
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public void setUndoer(Undoer undoer, Button undoButton, Button redoButton) {
        this.undoer = undoer;
        this.undoButton = undoButton;
        this.redoButton = redoButton;
    }

    public void setFocus(boolean focus){
        surface.setFocus(focus);
    }

    public void redraw(){
        drawer.redraw(formula);
    }

    public Drawer getDrawer() {
        return drawer;
    }

    private void addKeys(String text) {
        //
    }
    private boolean arrow = false;

    public void initKeyboadrHandling() {
        //Editor keyboard handlers
        surface.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                final int keycode = event.getNativeKeyCode();
                arrow = true;

                if (event.isLeftArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Left arrow - move selecting cursor left
                        addKeys(" Shift-Left");
                        editor.selectLeft();
                    } else {
                        //Left arrow - move cursor left
                        //addKeys(" Left");
                        editor.moveCursorLeft();
                    }
                } else if (event.isRightArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Rigth arrow - move selecting cursor right
                        addKeys(" Shift-Right");
                        editor.selectRight();
                    } else {
                        //Rigth arrow - move cursor right
                        addKeys(" Right");
                        editor.moveCursorRight();
                    }
                } else if (event.isUpArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Up arrow - move selecting cursor up
                        addKeys(" Shift-Up");
                        editor.selectUp();
                    } else {
                        //Up arrow
                        addKeys(" Up");
                        if (editor.isAutoCompletion()) {
                            //move cursor up
                            editor.moveAutoCompletionUp();
                        } else {
                            //move cursor up
                            editor.moveCursorUp();
                        }
                    }
                } else if (event.isDownArrow()) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Down arrow - move selecting cursor down
                        addKeys(" Shift-Down");
                        editor.selectDown();
                    } else {
                        //Down arrow
                        addKeys(" Down");
                        if (editor.isAutoCompletion()) {
                            //move cursor down
                            editor.moveAutoCompletionDown();
                        } else {
                            //move cursor down
                            editor.moveCursorDown();
                        }
                    }
                } else if (keycode == 36) {
                    //Home
                    editor.moveCursorFirst();
                } else if (keycode == 35) {
                    //End
                    editor.moveCursorLast();

                } else if (keycode == KeyCodes.KEY_ENTER) {
                    //Enter - auto-complete
                    addKeys(" Enter");
                    if (editor.isAutoCompletion()) {
                        editor.selectAutoCompletion();
                    }
                } else if (keycode == KeyCodes.KEY_ESCAPE) {
                    //Esc - hide auto-complete
                    addKeys(" Esc");
                    if (editor.isAutoCompletion()) {
                        editor.hideAutoCompletion();
                    }
                } else if (keycode == 32) {
                    if (event.isControlKeyDown()) {
                        //Ctrl-Space - show auto-complete
                        addKeys(" Ctrl-Space");
                        editor.showAutoCompletion();
                    } else {
                        //Space - move cursor right
                        addKeys(" Space");
                        editor.moveCursorRight();
                    }

                } else if (keycode == KeyCodes.KEY_DELETE) {
                    if (event.isShiftKeyDown()) {
                        //Shift-Del - cut selected
                        addKeys(" Shift-Del");
                        editor.cut();
                    } else {
                        //Del - delete to the right from cursor
                        addKeys(" Del");
                        editor.deleteRight();
                    }
                } else if (keycode == KeyCodes.KEY_BACKSPACE) {
                    if (event.isControlKeyDown()) {
                        //Ctrl-Backspace - undo
                        addKeys(" Ctrl-Backspace");
                        undoer.undo();
                        editor.redraw();
                    } else {
                        //Backspace - delete from the left from cursor
                        addKeys(" Backspace");
                        editor.deleteLeft();
                    }

                } else if (event.isControlKeyDown() && keycode == 65) {
                    //Ctrl-A - select all
                    addKeys(" Ctrl-A");
                    editor.selectAll();
                } else if (event.isControlKeyDown() && keycode == 88) {
                    //Ctrl-X - cut selected
                    addKeys(" Ctrl-X");
                    editor.cut();
                } else if (event.isShiftKeyDown() && keycode == 45) {
                    //Shift-Ins - paste
                    addKeys(" Shift-Ins");
                    editor.paste();
                } else if (event.isControlKeyDown() && keycode == 86) {
                    //Ctrl-V - paste
                    addKeys(" Ctrl-V");
                    editor.paste();
                } else if (event.isControlKeyDown() && keycode == 45) {
                    //Ctrl-Ins - copy selected
                    addKeys(" Ctrl-Ins");
                    editor.copy();
                } else if (event.isControlKeyDown() && keycode == 67) {
                    //Ctrl-C - copy selected
                    addKeys(" Ctrl-C");
                    editor.copy();

                } else if (event.isControlKeyDown() && keycode == 90) {
                    //Ctrl-Z - undo
                    addKeys(" Ctrl-Z");
                    undoer.undo();
                    editor.redraw();
                } else if (event.isControlKeyDown() && keycode == 89) {
                    //Ctrl-Y - redo
                    addKeys(" Ctrl-Y");
                    undoer.redo();
                    editor.redraw();

                } else if (event.isControlKeyDown()) {
                    addKeys("+p" + event.getNativeKeyCode());
                } else {
                    //addKeys("+p" + event.getNativeKeyCode());
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
                        addKeys(" '^'");
                        editor.moveCursorToPower();
                    } else {
                        addKeys(" '" + event.getCharCode() + "'");
                        editor.insert(event.getCharCode());
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

    }

    public void initMouseHandling() {
        //Editor mouse handlers
        surface.addMouseMoveHandler(new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
                //editor.highlightItemAt(event.getX(), event.getY());
                editor.mouseMoveAt(event.getX(), event.getY());
            }
        });

        surface.addMouseUpHandler(new MouseUpHandler() {

            public void onMouseUp(MouseUpEvent event) {
                //editor.selectItemAt(event.getX(), event.getY());
                editor.mouseUpAt(event.getX(), event.getY());
            }
        });

        surface.addMouseDownHandler(new MouseDownHandler() {

            public void onMouseDown(MouseDownEvent event) {
                //editor.selectItemAt(event.getX(), event.getY());
                editor.mouseDownAt(event.getX(), event.getY());
            }
        });
    }
}
