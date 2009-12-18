/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formed.client;

import java.util.HashMap;
import java.util.Map;
import net.kornr.abstractcanvas.client.ICanvasExt;
import net.kornr.abstractcanvas.client.TextMetrics;

/**
 *
 * @author bulats
 */
public class DrawableFormula {

    private final Formula formula1;
    private final Map<String, String> vars = new HashMap<String, String>();
    private final double SCALE = 3.0/4.0;

    public DrawableFormula(Formula formula) {
        this.formula1 = formula;

        vars.put("alpha", "&alpha;");
    }

    private String getVar(String variable) {
        String var = vars.get(variable);
        if (var == null) {
            return variable;
        }

        return var;
    }

    private Metrics measureFormula(ICanvasExt canvas, Formula formula, int fontSize) {
        int accWidth = 0;
        int accHeightUp = 0;
        int accHeightDown = 0;

        for (FormulaItem item : formula.getItems()) {
            canvas.setFontSize(fontSize);

            int width = 0;
            int heightUp = 0;
            int heightDown = 0;
            if (item instanceof Variable) {
                Variable itemV = (Variable) item;
                TextMetrics metrics = canvas.measureText(getVar(itemV.getVariable()));

                width = (int) metrics.getWidth();
                heightUp = (int) metrics.getHeight() / 2;
                heightDown = heightUp;

            } else if (item instanceof Operator) {
                Operator itemO = (Operator) item;
                TextMetrics metrics = canvas.measureText(itemO.getOperator());

                width = (int) metrics.getWidth();
                heightUp = (int) metrics.getHeight() / 2;
                heightDown = heightUp;

            } else if (item instanceof Function) {
                Function itemF = (Function) item;
                Formula formulaF = itemF.getFormula();

                TextMetrics metrics = canvas.measureText(itemF.getFunction() + "()");
                Metrics metricsF = measureFormula(canvas, formulaF, fontSize);

                width = (int) (metrics.getWidth() + metricsF.getWidth());
                heightUp = (int) Math.max(metricsF.getHeightUp(), metrics.getHeight() / 2);
                heightDown = (int) Math.max(metricsF.getHeightDown(), metrics.getHeight() / 2);

            } else if (item instanceof TwoArgFunction) {
                TwoArgFunction itemTAF = (TwoArgFunction) item;
                String function = itemTAF.getFunction();
                Formula formulaOne = itemTAF.getFormulaOne();
                Formula formulaTwo = itemTAF.getFormulaTwo();

                if ("pow".equals(function)) {
                    Metrics metricsFormula = measureFormula(canvas, formulaOne, fontSize);
                    Metrics metricsPower = measureFormula(canvas, formulaTwo, (int)(fontSize * SCALE));

                    width = metricsFormula.getWidth() + metricsPower.getWidth();
                    heightUp = metricsFormula.getHeightUp() + metricsPower.getHeightUp();
//                    heightUp = metricsFormula.getHeightUp() + metricsPower.getHeight();
                    heightDown = metricsFormula.getHeightDown();
                } else if ("div".equals(function)) {
                    Metrics metricsUp = measureFormula(canvas, formulaOne, fontSize);
                    Metrics metricsDown = measureFormula(canvas, formulaTwo, fontSize);

                    width = Math.max(metricsUp.getWidth(), metricsDown.getWidth());
                    heightUp = metricsUp.getHeight() + 1;
                    heightDown = metricsDown.getHeight() + 1;
                } else if ("root".equals(function)) {
                    TextMetrics metrics = canvas.measureText("a");
                    Metrics metricsFormula = measureFormula(canvas, formulaOne, fontSize);
                    Metrics metricsPower = measureFormula(canvas, formulaTwo, (int)(fontSize * SCALE));

                    width = metricsFormula.getWidth() + metricsPower.getWidth() + 8;
                    int heightMiddle = (int) (metricsFormula.getHeight() / 2 - metrics.getHeight() / 2);
                    int heightTop = (int) (metricsFormula.getHeight() - metrics.getHeight() / 2 + 1);

                    heightUp = Math.max(heightTop, heightMiddle + metricsPower.getHeight()) + 2;
                    heightDown = (int) (metrics.getHeight() / 2) + 1;
                } else {
                }

            } else {
            }

            accWidth += width;
            if (heightUp > accHeightUp) {
                accHeightUp = heightUp;
            }
            if (heightDown > accHeightDown) {
                accHeightDown = heightDown;
            }
        }

        return new Metrics(accWidth, accHeightUp, accHeightDown);
    }

    private void drawFormula(ICanvasExt canvas, Formula formula, int posX, int posY, int fontSize) {
        for (FormulaItem item : formula.getItems()) {
            canvas.setFontSize(fontSize);

            if (item instanceof Variable) {
                Variable itemV = (Variable) item;
                String variable = getVar(itemV.getVariable());
                TextMetrics metrics = canvas.measureText(variable);

                posX += metrics.getWidth();

                canvas.strokeText(variable, posX, (int) (posY - metrics.getHeight() / 2));

            } else if (item instanceof Operator) {
                Operator itemO = (Operator) item;
                TextMetrics metrics = canvas.measureText(itemO.getOperator());

                posX += metrics.getWidth();

                canvas.strokeText(itemO.getOperator(), posX, (int) (posY - metrics.getHeight() / 2));

            } else if (item instanceof Function) {
                Function itemF = (Function) item;
                String function = itemF.getFunction();
                Formula formulaF = itemF.getFormula();

                TextMetrics metrics1 = canvas.measureText(function + "(");
                TextMetrics metrics2 = canvas.measureText(")");
                Metrics metricsF = measureFormula(canvas, formulaF, fontSize);

                posX += metrics1.getWidth();
                canvas.strokeText(function + "(", posX, (int) (posY - metrics1.getHeight() / 2));

                drawFormula(canvas, formulaF, posX, posY, fontSize);

                posX += metricsF.getWidth() + metrics2.getWidth();
                canvas.strokeText(")", posX, (int) (posY - metrics2.getHeight() / 2));

            } else if (item instanceof TwoArgFunction) {
                TwoArgFunction itemTAF = (TwoArgFunction) item;
                String function = itemTAF.getFunction();
                Formula formulaOne = itemTAF.getFormulaOne();
                Formula formulaTwo = itemTAF.getFormulaTwo();

                if ("pow".equals(function)) {
                    Metrics metricsFormula = measureFormula(canvas, formulaOne, fontSize);
                    Metrics metricsPower = measureFormula(canvas, formulaTwo, (int)(fontSize * SCALE));

                    drawFormula(canvas, formulaOne, posX, posY, fontSize);
                    posX += metricsFormula.getWidth();

                    drawFormula(canvas, formulaTwo, posX, posY - metricsFormula.getHeightUp(), (int)(fontSize * SCALE));
//                    drawFormula(canvas, formulaTwo, posX, posY - metricsFormula.getHeightUp() - metricsPower.getHeightDown(), (int)(fontSize * SCALE));
                    posX += metricsPower.getWidth();
                } else if ("div".equals(function)) {
                    Metrics metricsUp = measureFormula(canvas, formulaOne, fontSize);
                    Metrics metricsDown = measureFormula(canvas, formulaTwo, fontSize);

                    int width = Math.max(metricsUp.getWidth(), metricsDown.getWidth());

                    canvas.beginPath();
                    canvas.moveTo(posX, posY);
                    canvas.lineTo(posX + width, posY);
                    canvas.stroke();

                    drawFormula(canvas, formulaOne, posX + width / 2 - metricsUp.getWidth() / 2, posY - metricsUp.getHeightDown() - 1, fontSize);
                    drawFormula(canvas, formulaTwo, posX + width / 2 - metricsDown.getWidth() / 2, posY + metricsDown.getHeightUp() + 1, fontSize);

                    posX += width;
                } else if ("root".equals(function)) {
                    TextMetrics metrics = canvas.measureText("a");
                    Metrics metricsFormula = measureFormula(canvas, formulaOne, fontSize);
                    Metrics metricsPower = measureFormula(canvas, formulaTwo, (int)(fontSize * SCALE));

                    int heightMiddle = (int) (metricsFormula.getHeight() / 2 - metrics.getHeight() / 2);
                    int heightTop = (int) (metricsFormula.getHeight() - metrics.getHeight() / 2 + 1);

                    drawFormula(canvas, formulaTwo, posX, posY - heightMiddle - metricsPower.getHeightDown(), (int)(fontSize * SCALE));

                    canvas.beginPath();
                    canvas.moveTo(posX, posY - heightMiddle);
                    posX += metricsPower.getWidth();
                    canvas.lineTo(posX, posY - heightMiddle);
                    posX += 3;
                    canvas.lineTo(posX, posY + metrics.getHeight() / 2);
                    posX += 3;
                    canvas.lineTo(posX, posY - heightTop);
                    canvas.lineTo(posX + metricsFormula.getWidth() + 2, posY - heightTop);
                    canvas.lineTo(posX + metricsFormula.getWidth() + 2, posY - heightTop + 4);
                    canvas.stroke();

                    drawFormula(canvas, formulaOne, posX, (int) (posY - metricsFormula.getHeightDown() + metrics.getHeight() / 2), fontSize);
                    posX += metricsFormula.getWidth() + 2;
                } else {
                }

            } else {
            }
        }
    }

    public Metrics draw(ICanvasExt canvas, int posX, int posY, int fontSize) {
        canvas.setFontSize(fontSize);
        Metrics metrics = measureFormula(canvas, formula1, fontSize);
        drawFormula(canvas, formula1, posX, posY + metrics.getHeightUp(), fontSize);

        return metrics;
    }
}
