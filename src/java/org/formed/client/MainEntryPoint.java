/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formed.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import java.util.List;
import net.kornr.abstractcanvas.client.CanvasPainter;
import net.kornr.abstractcanvas.client.ICanvasExt;
import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;
import org.formed.client.formula.DrawExample;

/**
 * Main entry point.
 *
 * @author Bulat Sirazetdinov
 */
public class MainEntryPoint implements EntryPoint {

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }

    /** 
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {
        DrawExample example = new DrawExample();
        example.draw();
//        final GWTCanvas canvas = new GWTCanvas(10, 10, 200, 200);
//
//        final Formula formula = new Formula();
//        final DrawableFormula drawableFormula = new DrawableFormula(formula);
//
//        List<FormulaItem> items = formula.getItems();
//        items.add(new Variable(formula, "a"));
//        items.add(new Operator(formula, "="));
//        items.add(new Variable(formula, "alpha"));
//        items.add(new Operator(formula, "+"));
//
//        TwoArgFunction div1 = new TwoArgFunction(formula, "div");
//        items.add(div1);
//        div1.getFormulaOne().getItems().add(new Variable(div1.getFormulaOne(), "a"));
//        div1.getFormulaOne().getItems().add(new Operator(div1.getFormulaOne(), "+"));
//
//        div1.getFormulaTwo().getItems().add(new Variable(div1.getFormulaTwo(), "b"));
//        div1.getFormulaTwo().getItems().add(new Operator(div1.getFormulaTwo(), "+"));
//
//        TwoArgFunction div1up = new TwoArgFunction(div1.getFormulaOne(), "pow");
//        div1.getFormulaOne().getItems().add(div1up);
//        div1up.getFormulaTwo().getItems().add(new Variable(div1up.getFormulaTwo(), "2"));
//        List<FormulaItem> div1upItems = div1up.getFormulaOne().getItems();
//        div1upItems.add(new Variable(div1up.getFormulaOne(), "("));
//        div1upItems.add(new Variable(div1up.getFormulaOne(), "x"));
//        div1upItems.add(new Operator(div1up.getFormulaOne(), "+"));
//        div1upItems.add(new Variable(div1up.getFormulaOne(), "y"));
//        div1upItems.add(new Variable(div1up.getFormulaOne(), ")"));
//
//        Function func1 = new Function(div1.getFormulaTwo(), "sin");
//        div1.getFormulaTwo().getItems().add(func1);
//        func1.getFormula().getItems().add(new Variable(func1.getFormula(), "alpha"));
//
//        div1.getFormulaTwo().getItems().add(new Operator(div1.getFormulaTwo(), "+"));
//
//        TwoArgFunction div2 = new TwoArgFunction(formula, "div");
//        div1.getFormulaTwo().getItems().add(div2);
//        div2.getFormulaOne().getItems().add(new Variable(div2.getFormulaOne(), "a"));
//
//        div2.getFormulaTwo().getItems().add(new Variable(div2.getFormulaTwo(), "a"));
//        div2.getFormulaTwo().getItems().add(new Operator(div2.getFormulaTwo(), "+"));
//
//        TwoArgFunction root1 = new TwoArgFunction(formula, "root");
//        div2.getFormulaTwo().getItems().add(root1);
//
//        TwoArgFunction exp1 = new TwoArgFunction(formula, "pow");
//        root1.getFormulaTwo().getItems().add(exp1);
//        exp1.getFormulaOne().getItems().add(new Variable(root1.getFormulaTwo(), "A"));
//        exp1.getFormulaTwo().getItems().add(new Variable(root1.getFormulaTwo(), "X"));
//
//        List<FormulaItem> root1Items = root1.getFormulaOne().getItems();
//        root1Items.add(new Variable(root1.getFormulaOne(), "x"));
//        root1Items.add(new Operator(root1.getFormulaOne(), "+"));
//
//        TwoArgFunction div3 = new TwoArgFunction(formula, "div");
//        root1Items.add(div3);
//        div3.getFormulaOne().getItems().add(new Variable(div3.getFormulaOne(), "a"));
//        div3.getFormulaTwo().getItems().add(new Variable(div3.getFormulaTwo(), "b"));
//
//        div2.getFormulaTwo().getItems().add(new Operator(div2.getFormulaTwo(), "+"));
//        div2.getFormulaTwo().getItems().add(new Variable(div2.getFormulaTwo(), "b"));
//
//
//        canvas.setCoordSize(200, 200);
//
//        canvas.setBackgroundColor(Color.WHITE);
//
//        canvas.setFillStyle(Color.BLUEVIOLET);
//        canvas.fillRect(30, 30, 40, 40);
//
//        canvas.setStrokeStyle(Color.ORANGE);
//        canvas.beginPath();
//        canvas.moveTo(10, 10);
//        canvas.lineTo(20, 50);
//        canvas.stroke();
//
//        final Label label = new Label("Hello, GWT!!!");
//        final Button button = new Button("Click me!");
//
//        button.addClickHandler(new ClickHandler() {
//
//            public void onClick(ClickEvent event) {
//                label.setVisible(!label.isVisible());
//
//                canvas.clear();
//
//                canvas.setStrokeStyle(Color.DARK_BLUE);
//                canvas.beginPath();
//                canvas.moveTo(0, 0);
//                canvas.lineTo(30, 100);
//                canvas.stroke();
//
//                RootPanel.get().add(new HTML("W" + canvas.getCoordWidth()));
//
//            }
//        });
//
//        RootPanel.get().add(canvas);
//        RootPanel.get().add(button);
//        RootPanel.get().add(label);
//        final CanvasPanelExt canvas1 = new CanvasPanelExt(400, 150);
//        canvas1.setCoordSize(400, 150);
//
//        canvas1.addCanvasPainter(new CanvasPainter() {
//
//            public void drawCanvas(ICanvasExt canvas) {
//                canvas.setAlign(net.kornr.abstractcanvas.client.gwt.CanvasPanelExt.ALIGN_END);
//                canvas.setFillStyle(Color.BLUE);
//                canvas.setGlobalAlpha(1.0);
//                canvas.fillRect(0, 0, canvas1.getCoordWidth(), canvas1.getCoordHeight());
//
//                canvas.setStrokeStyle(Color.BLUEVIOLET);
//                canvas.setLineWidth(1);
//                Metrics metrics = drawableFormula.draw(canvas, 10, 10, 20);
//                canvas.beginPath();
//                canvas.rect(9, 9, 2+metrics.getWidth(), 2+metrics.getHeight());
//                canvas.stroke();
//
///*
//                canvas.setStrokeStyle(Color.BLUEVIOLET);
//                canvas.setLineWidth(1);
//                canvas.beginPath();
//                canvas.moveTo(10, 100);
//                canvas.lineTo(100, 100);
//                canvas.rect(10, 10, 50, 30);
//                canvas.stroke();
//                canvas.strokeText("Hello world!", 100, 100);*/
//            }
//        });
//
//        RootPanel.get().add(canvas1);

    }
}
