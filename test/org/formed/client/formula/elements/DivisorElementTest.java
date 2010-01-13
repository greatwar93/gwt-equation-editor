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
package org.formed.client.formula.elements;

import net.kornr.abstractcanvas.client.gwt.CanvasPanelExt;
import org.formed.client.formula.Cursor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.Drawer;
import org.formed.client.formula.FormulaItem;
import org.formed.client.formula.Metrics;
import org.formed.client.formula.Rectangle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bulats
 */
public class DivisorElementTest {

    Drawer drawer;

    public DivisorElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        drawer = new DrawerMock();
    }

    @After
    public void tearDown() {
    }

//    /**
//     * Test of getFormula1 method, of class DivisorElement.
//     */
//    @Test
//    public void testGetFormula1() {
//        System.out.println("getFormula1");
//        DivisorElement instance = new DivisorElement();
//        Formula expResult = null;
//        Formula result = instance.getFormula1();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFormula1 method, of class DivisorElement.
//     */
//    @Test
//    public void testSetFormula1() {
//        System.out.println("setFormula1");
//        Formula formula1 = null;
//        DivisorElement instance = new DivisorElement();
//        instance.setFormula1(formula1);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFormula2 method, of class DivisorElement.
//     */
//    @Test
//    public void testGetFormula2() {
//        System.out.println("getFormula2");
//        DivisorElement instance = new DivisorElement();
//        Formula expResult = null;
//        Formula result = instance.getFormula2();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFormula2 method, of class DivisorElement.
//     */
//    @Test
//    public void testSetFormula2() {
//        System.out.println("setFormula2");
//        Formula formula2 = null;
//        DivisorElement instance = new DivisorElement();
//        instance.setFormula2(formula2);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isComplex method, of class DivisorElement.
//     */
//    @Test
//    public void testIsComplex() {
//        System.out.println("isComplex");
//        DivisorElement instance = new DivisorElement();
//        boolean expResult = false;
//        boolean result = instance.isComplex();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of draw method, of class DivisorElement.
//     */
//    @Test
//    public void testDraw() {
//        System.out.println("draw");
//        FormulaDrawer drawer = null;
//        int x = 0;
//        int y = 0;
//        int size = 0;
//        DivisorElement instance = new DivisorElement();
//        Metrics expResult = null;
//        Metrics result = instance.draw(drawer, x, y, size);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of measure method, of class DivisorElement.
//     */
//    @Test
//    public void testMeasure() {
//        System.out.println("measure");
//        FormulaDrawer drawer = null;
//        int size = 0;
//        DivisorElement instance = new DivisorElement();
//        Metrics expResult = null;
//        Metrics result = instance.measure(drawer, size);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCursor method, of class DivisorElement.
//     */
//    @Test
//    public void testGetCursor_3args() {
//        System.out.println("getCursor");
//        FormulaDrawer drawer = null;
//        int x = 0;
//        int y = 0;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getCursor(drawer, x, y);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCursor method, of class DivisorElement.
//     */
//    @Test
//    public void testGetCursor_FormulaDrawer_int() {
//        System.out.println("getCursor");
//        FormulaDrawer drawer = null;
//        int position = 0;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getCursor(drawer, position);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFirst method, of class DivisorElement.
//     */
//    @Test
//    public void testGetFirst() {
//        System.out.println("getFirst");
//        FormulaDrawer drawer = null;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getFirst(drawer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLast method, of class DivisorElement.
//     */
//    @Test
//    public void testGetLast() {
//        System.out.println("getLast");
//        FormulaDrawer drawer = null;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getLast(drawer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLeft method, of class DivisorElement.
//     */
//    @Test
//    public void testGetLeft() {
//        System.out.println("getLeft");
//        FormulaDrawer drawer = null;
//        int oldPosition = 0;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getLeft(drawer, oldPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRight method, of class DivisorElement.
//     */
//    @Test
//    public void testGetRight() {
//        System.out.println("getRight");
//        FormulaDrawer drawer = null;
//        int oldPosition = 0;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getRight(drawer, oldPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksLeft method, of class DivisorElement.
//     */
//    @Test
//    public void testChildAsksLeft() {
//        System.out.println("childAsksLeft");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksLeft(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksRight method, of class DivisorElement.
//     */
//    @Test
//    public void testChildAsksRight() {
//        System.out.println("childAsksRight");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksRight(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksUp method, of class DivisorElement.
//     */
//    @Test
//    public void testChildAsksUp() {
//        System.out.println("childAsksUp");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksUp(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksDown method, of class DivisorElement.
//     */
//    @Test
//    public void testChildAsksDown() {
//        System.out.println("childAsksDown");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        DivisorElement instance = new DivisorElement();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksDown(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of invalidateMetrics method, of class DivisorElement.
     */
    @Test
    public void testInvalidateMetrics() {
        System.out.println("invalidateMetrics");

        FormulaMock formula = new FormulaMock();

        FormulaMock formula1 = new FormulaMock();
        FormulaMock formula2 = new FormulaMock();

        DivisorElement instance = new DivisorElement(formula1, formula2);
        formula.add(instance);

        FormulaItem initiator = new SimpleElement("a");
        formula1.add(initiator).add(new OperatorElement("+")).add(new SimpleElement("b"));
        formula2.add(new SimpleElement("x")).add(new OperatorElement("*")).add(new SimpleElement("y"));

        Cursor cursor = initiator.getLast(drawer);

        formula.setInvalidated(0);
        formula1.setInvalidated(0);
        formula2.setInvalidated(0);

        initiator.insertChar(drawer, cursor, 'c');

        assertEquals("Parent propogation", 1, formula.getInvalidated());
        assertEquals("Child1 propogation", 1, formula1.getInvalidated());
        assertEquals("Child2 propogation", 1, formula2.getInvalidated());
    }
}
