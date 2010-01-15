/*
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.g2d.client.graphics.Surface;
import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class DrawExample {

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
        formula.add(new DivisorElement(new Formula().add(new SimpleElement("a", new Formula().add(new RootElement(new Formula().add(new SimpleElement("α")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("alpha")).add(new SimpleElement("b", new Formula().add(new SimpleElement("2")))))))).add(new OperatorElement("+")).add(new SimpleElement("b")), new Formula().add(new RootElement(new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new DivisorElement(new Formula().add(new SimpleElement("beta")), new Formula().add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("β")), new Formula().add(new SimpleElement("2"))))))))));
        formula.add(new OperatorElement("+"));
//        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")).add(new OperatorElement("+")).add(new SimpleElement("beta")), new Formula().add(new SimpleElement("2"))));
//        formula.add(new OperatorElement("+"));
        formula.add(new FunctionElement("sin", new Formula().add(new SimpleElement("alpha")), new Formula().add(new SimpleElement("e", new Formula().add(new SimpleElement("x"))))));

        final int WIDTH = 630;
        final int HEIGHT = 150;

//        final CanvasPanelExt canvas1 = new CanvasPanelExt(WIDTH, HEIGHT);
//        canvas1.setCoordSize(WIDTH, HEIGHT);

        final Surface surface = new Surface(WIDTH, HEIGHT);
        final SurfaceDrawer drawer = new SurfaceDrawer(surface, formula);

        //        RootPanel.get().add(canvas1, 10, 10);
        RootPanel.get().add(surface, 10, 10);
        /*
        FocusPanel panel = new FocusPanel();
        panel.setSize(Integer.toString(WIDTH), Integer.toString(HEIGHT));

        panel.addKeyDownHandler(new KeyDownHandler() {

        public void onKeyDown(KeyDownEvent event) {
        if (event.isLeftArrow()) {
        drawer.moveCursorLeft();
        } else if (event.isRightArrow()) {
        drawer.moveCursorRight();
        } else if (event.isUpArrow()) {
        drawer.moveCursorUp();
        } else if (event.isDownArrow()) {
        drawer.moveCursorDown();
        }

        //                canvas1.strokeText("code:"+event.isLeftArrow(), 300, 10);
        //                canvas1.strokeText("code:"+event.getNativeKeyCode(), 300, 30);
        }
        });

        panel.addKeyPressHandler(new KeyPressHandler() {

        public void onKeyPress(KeyPressEvent event) {
        if (event.getCharCode() == '%') {
        //drawer.moveCursorLeft();
        } else if (event.getCharCode() == '\'') {
        //drawer.moveCursorRight();
        } else if (event.getCharCode() == '&') {
        //drawer.moveCursorUp();
        } else if (event.getCharCode() == '(') {
        //drawer.moveCursorDown();
        } else {
        drawer.insert(event.getCharCode());
        }

        //                canvas1.strokeText("code:" + event.getCharCode(), 200, 10);
        //                canvas1.strokeText("code:" + event.getAssociatedType().getName(), 200, 40);
        }
        });

        panel.addMouseMoveHandler(new MouseMoveHandler() {

        public void onMouseMove(MouseMoveEvent event) {
        //                drawer.highlightItemAt(event.getX(), event.getY());
        }
        });

        panel.addMouseUpHandler(new MouseUpHandler() {

        public void onMouseUp(MouseUpEvent event) {
        drawer.selectItemAt(event.getX(), event.getY());
        }
        });

        RootPanel.get().add(panel, 10, 10);
         */
        surface.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                int keycode = event.getNativeKeyCode();
                if (event.isLeftArrow()) {
                    drawer.moveCursorLeft();
                } else if (event.isRightArrow()) {
                    drawer.moveCursorRight();
                } else if (event.isUpArrow()) {
                    drawer.moveCursorUp();
                } else if (event.isDownArrow()) {
                    drawer.moveCursorDown();
                } else if (keycode == KeyCodes.KEY_DELETE) {
                    drawer.deleteRight();
                } else if (keycode == KeyCodes.KEY_BACKSPACE) {
                    drawer.deleteLeft();
                }

//                canvas1.strokeText("code:"+event.isLeftArrow(), 300, 10);
//                canvas1.strokeText("code:"+event.getNativeKeyCode(), 300, 30);
            }
        });
        /*
        surface.addKeyUpHandler(new KeyUpHandler() {

        public void onKeyUp(KeyUpEvent event) {
        int keycode = event.getNativeKeyCode();
        if (event.isLeftArrow()) {
        } else if (event.isRightArrow()) {
        } else if (event.isUpArrow()) {
        } else if (event.isDownArrow()) {
        } else if(keycode == KeyCodes.KEY_DELETE){
        } else if(keycode == KeyCodes.KEY_BACKSPACE){
        }else{
        drawer.insert((char)keycode);
        }
        }
        });
         */
        final HTML keys = new HTML();
        RootPanel.get().add(keys, 10, HEIGHT * 2);

        surface.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                keys.setHTML(keys.getHTML() + "+" + event.getCharCode());
                if (event.getCharCode() == '%') {
                    //drawer.moveCursorLeft();
                } else if (event.getCharCode() == '\'') {
                    //drawer.moveCursorRight();
                } else if (event.getCharCode() == '&') {
                    //drawer.moveCursorUp();
                } else if (event.getCharCode() == '(') {
                    //drawer.moveCursorDown();
                } else if (event.getCharCode() == '.') {
                    //drawer.deleteRight();
                } else if (event.getCharCode() == 8) {
                    //drawer.deleteLeft();
                } else {
                    drawer.insert(event.getCharCode());
                }

                //                canvas1.strokeText("code:" + event.getCharCode(), 200, 10);
//                canvas1.strokeText("code:" + event.getAssociatedType().getName(), 200, 40);
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

        drawer.redraw();

    }
}
