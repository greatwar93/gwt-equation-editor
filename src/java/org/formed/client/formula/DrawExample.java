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
package org.formed.client.formula;

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
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.g2d.client.graphics.Surface;
import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;
import org.formed.client.formula.impl.SimpleUndoer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class DrawExample {

    boolean arrow = false;

    public void draw() {
        final Formula formula = new Formula();

        formula.add(new SimpleElement("S"));
        formula.add(new OperatorElement("="));
        formula.add(new SimpleElement("a", new Formula().add(new SimpleElement("2"))));
        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("α"))));
        formula.add(new OperatorElement("-"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a")).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta"))));
        formula.add(new OperatorElement("+"));
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a", new Formula().add(new RootElement(new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("alpha")).add(new SimpleElement("b", new Formula().add(new SimpleElement("2")))))))).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new RootElement(new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new DivisorElement(new Formula().add(new SimpleElement("beta")), new Formula().add(new FunctionElement("sin", new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new SimpleElement("β")), new Formula().add(new SimpleElement("2"))))))))));
        formula.add(new OperatorElement("+"));
//        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("2"))));
//        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("α")), new Formula().add(new SimpleElement("e", new Formula().add(new SimpleElement("x"))))));

        final int WIDTH = 630;
        final int HEIGHT = 150;

//        final CanvasPanelExt canvas1 = new CanvasPanelExt(WIDTH, HEIGHT);
//        canvas1.setCoordSize(WIDTH, HEIGHT);

        final Surface surface = new Surface(WIDTH, HEIGHT);
        final SimpleUndoer undoer = new SimpleUndoer();
        final SurfaceDrawer drawer = new SurfaceDrawer(surface, undoer, formula);

        //        RootPanel.get().add(canvas1, 10, 10);
        RootPanel.get().add(surface, 10, 10);
        /*FocusPanel panel = new FocusPanel();
        panel.setSize(Integer.toString(WIDTH), Integer.toString(HEIGHT));
        RootPanel.get().add(panel, 10, 10);*/

        final HTML keys = new HTML();
        RootPanel.get().add(keys, 10, HEIGHT * 3 + 10);

        final Button undoButton = new Button("Undo");
        final Button redoButton = new Button("Redo");

        undoButton.setEnabled(false);
        undoButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                undoer.undo();
                drawer.redraw();
                surface.setFocus(true);

                /*                Metrics drawerMetrics = drawer.getDrawerMetrics();
                surface.setWidth(drawerMetrics.getWidth()+20);
                surface.setHeight(drawerMetrics.getHeight()+20);
                drawer.redraw();*/

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

                /*                Metrics drawerMetrics = drawer.getDrawerMetrics();
                surface.setWidth(drawerMetrics.getWidth()+20);
                surface.setHeight(drawerMetrics.getHeight()+20);
                drawer.redraw();*/

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        RootPanel.get().add(undoButton, 10, HEIGHT + 20);
        RootPanel.get().add(redoButton, undoButton.getOffsetWidth() + 20, HEIGHT + 20);

        surface.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                final int keycode = event.getNativeKeyCode();
                arrow = true;
                if (event.isLeftArrow()) {
                    //keys.setHTML(keys.getHTML() + "+dLeft");
                    drawer.moveCursorLeft();
                } else if (event.isRightArrow()) {
                    drawer.moveCursorRight();
                    //keys.setHTML(keys.getHTML() + "+dRight");
                } else if (event.isUpArrow()) {
                    drawer.moveCursorUp();
                    //keys.setHTML(keys.getHTML() + "+dUp");
                } else if (event.isDownArrow()) {
                    drawer.moveCursorDown();
                    //keys.setHTML(keys.getHTML() + "+dDown");
                } else if (keycode == KeyCodes.KEY_DELETE) {
                    drawer.deleteRight();
                    //keys.setHTML(keys.getHTML() + "+dDel");
                } else if (keycode == KeyCodes.KEY_BACKSPACE) {
                    drawer.deleteLeft();
                    //keys.setHTML(keys.getHTML() + "+d<-");
/*                } else if (keycode == KeyCodes.KEY_ESCAPE) {
                    undoer.undo();
                    } else if (keycode == KeyCodes.KEY_TAB) {
                    undoer.redo();*/
                } else if (event.isControlKeyDown() && keycode == 45) {
                    //keys.setHTML(keys.getHTML() + "+dPaste");
                } else if (event.isControlKeyDown() && keycode == 86) {
                    //keys.setHTML(keys.getHTML() + "+dPaste");
                } else if (event.isShiftKeyDown() && keycode == 45) {
                    //keys.setHTML(keys.getHTML() + "+dCopy");
                } else if (event.isControlKeyDown() && keycode == 67) {
                    //keys.setHTML(keys.getHTML() + "+dCopy");
                } else if (event.isControlKeyDown() && (keycode == 90 || keycode == KeyCodes.KEY_BACKSPACE)) {
                    undoer.undo();
                    drawer.redraw();
                } else if (event.isControlKeyDown() && keycode == 89) {
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

                /*                Metrics drawerMetrics = drawer.getDrawerMetrics();
                surface.setWidth(drawerMetrics.getWidth()+20);
                surface.setHeight(drawerMetrics.getHeight()+20);
                drawer.redraw();*/

                undoButton.setEnabled(undoer.getUndoCount() > 0);
                redoButton.setEnabled(undoer.getRedoCount() > 0);
            }
        });

        surface.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (!arrow) {
                    drawer.insert(event.getCharCode());
                    //keys.setHTML(keys.getHTML() + "+p" + event.getCharCode());
                } else {
                    arrow = false;
                }
                event.preventDefault();

                /*                Metrics drawerMetrics = drawer.getDrawerMetrics();
                surface.setWidth(drawerMetrics.getWidth()+20);
                surface.setHeight(drawerMetrics.getHeight()+20);
                drawer.redraw();*/

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


        surface.addMouseMoveHandler(new MouseMoveHandler() {

            public void onMouseMove(MouseMoveEvent event) {
//                drawer.highlightItemAt(event.getX(), event.getY());
            }
        });

        surface.addMouseUpHandler(new MouseUpHandler() {

            public void onMouseUp(MouseUpEvent event) {
                drawer.selectItemAt(event.getX(), event.getY());
            }
        });

        final Button sinButton = new Button("sin", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("sin"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button cosButton = new Button("cos", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("cos"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button tgButton = new Button("tg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("tg"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button ctgButton = new Button("ctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("ctg"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button arcsinButton = new Button("arcsin", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arcsin"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button arccosButton = new Button("arccos", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arccos"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button arctgButton = new Button("arctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arctg"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button arcctgButton = new Button("arcctg", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new FunctionElement("arcctg"));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final Button rootButton = new Button("root", new ClickHandler() {

            public void onClick(ClickEvent event) {
                drawer.insertElement(new RootElement(new Formula()));
                drawer.redraw();
                surface.setFocus(true);
            }
        });

        final int BUTTON_WIDTH = 50;
        sinButton.setWidth(BUTTON_WIDTH+"px");
        cosButton.setWidth(BUTTON_WIDTH+"px");
        tgButton.setWidth(BUTTON_WIDTH+"px");
        ctgButton.setWidth(BUTTON_WIDTH+"px");
        rootButton.setWidth(BUTTON_WIDTH+"px");
        arcsinButton.setWidth(BUTTON_WIDTH+"px");
        arccosButton.setWidth(BUTTON_WIDTH+"px");
        arctgButton.setWidth(BUTTON_WIDTH+"px");
        arcctgButton.setWidth(BUTTON_WIDTH+"px");

        RootPanel.get().add(sinButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 50);
        RootPanel.get().add(cosButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 50);
        RootPanel.get().add(tgButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 50);
        RootPanel.get().add(ctgButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 50);
        RootPanel.get().add(rootButton, 10 + (10 + BUTTON_WIDTH) * 4, HEIGHT + 50);

        RootPanel.get().add(arcsinButton, 10 + (10 + BUTTON_WIDTH) * 0, HEIGHT + 80);
        RootPanel.get().add(arccosButton, 10 + (10 + BUTTON_WIDTH) * 1, HEIGHT + 80);
        RootPanel.get().add(arctgButton, 10 + (10 + BUTTON_WIDTH) * 2, HEIGHT + 80);
        RootPanel.get().add(arcctgButton, 10 + (10 + BUTTON_WIDTH) * 3, HEIGHT + 80);

        drawer.redraw();

    }
}
