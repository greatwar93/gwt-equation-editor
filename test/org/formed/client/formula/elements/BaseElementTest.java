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

import org.formed.client.formula.Cursor;
import org.formed.client.formula.Formula;
import org.formed.client.formula.FormulaDrawer;
import org.formed.client.formula.Metrics;
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
public class BaseElementTest {

    FormulaDrawer drawer;

    public BaseElementTest() {
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
//     * Test of getParent method, of class BaseElement.
//     */
//    @Test
//    public void testGetParent() {
//        System.out.println("getParent");
//        BaseElement instance = new BaseElementImpl();
//        Formula expResult = null;
//        Formula result = instance.getParent();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setParent method, of class BaseElement.
//     */
//    @Test
//    public void testSetParent() {
//        System.out.println("setParent");
//        Formula parent = null;
//        BaseElement instance = new BaseElementImpl();
//        instance.setParent(parent);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of draw method, of class BaseElement.
//     */
//    @Test
//    public void testDraw() {
//        System.out.println("draw");
//        FormulaDrawer drawer = null;
//        int x = 0;
//        int y = 0;
//        int size = 0;
//        BaseElement instance = new BaseElementImpl();
//        Metrics expResult = null;
//        Metrics result = instance.draw(drawer, x, y, size);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of measure method, of class BaseElement.
//     */
//    @Test
//    public void testMeasure() {
//        System.out.println("measure");
//        FormulaDrawer drawer = null;
//        int size = 0;
//        BaseElement instance = new BaseElementImpl();
//        Metrics expResult = null;
//        Metrics result = instance.measure(drawer, size);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLength method, of class BaseElement.
//     */
//    @Test
//    public void testGetLength() {
//        System.out.println("getLength");
//        BaseElement instance = new BaseElementImpl();
//        int expResult = 0;
//        int result = instance.getLength();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCursor method, of class BaseElement.
//     */
//    @Test
//    public void testGetCursor_3args() {
//        System.out.println("getCursor");
//        FormulaDrawer drawer = null;
//        int x = 0;
//        int y = 0;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getCursor(drawer, x, y);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLeft method, of class BaseElement.
//     */
//    @Test
//    public void testGetLeft() {
//        System.out.println("getLeft");
//        FormulaDrawer drawer = null;
//        int oldPosition = 0;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getLeft(drawer, oldPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRight method, of class BaseElement.
//     */
//    @Test
//    public void testGetRight() {
//        System.out.println("getRight");
//        FormulaDrawer drawer = null;
//        int oldPosition = 0;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getRight(drawer, oldPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getUp method, of class BaseElement.
//     */
//    @Test
//    public void testGetUp() {
//        System.out.println("getUp");
//        FormulaDrawer drawer = null;
//        int oldPosition = 0;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getUp(drawer, oldPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDown method, of class BaseElement.
//     */
//    @Test
//    public void testGetDown() {
//        System.out.println("getDown");
//        FormulaDrawer drawer = null;
//        int oldPosition = 0;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getDown(drawer, oldPosition);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCursor method, of class BaseElement.
//     */
//    @Test
//    public void testGetCursor_FormulaDrawer_int() {
//        System.out.println("getCursor");
//        FormulaDrawer drawer = null;
//        int position = 0;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getCursor(drawer, position);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFirst method, of class BaseElement.
//     */
//    @Test
//    public void testGetFirst() {
//        System.out.println("getFirst");
//        FormulaDrawer drawer = null;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getFirst(drawer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLast method, of class BaseElement.
//     */
//    @Test
//    public void testGetLast() {
//        System.out.println("getLast");
//        FormulaDrawer drawer = null;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.getLast(drawer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksLeft method, of class BaseElement.
//     */
//    @Test
//    public void testChildAsksLeft() {
//        System.out.println("childAsksLeft");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksLeft(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksRight method, of class BaseElement.
//     */
//    @Test
//    public void testChildAsksRight() {
//        System.out.println("childAsksRight");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksRight(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksUp method, of class BaseElement.
//     */
//    @Test
//    public void testChildAsksUp() {
//        System.out.println("childAsksUp");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksUp(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of childAsksDown method, of class BaseElement.
//     */
//    @Test
//    public void testChildAsksDown() {
//        System.out.println("childAsksDown");
//        FormulaDrawer drawer = null;
//        Formula child = null;
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.childAsksDown(drawer, child);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of invalidateMetrics method, of class BaseElement.
     */
    @Test
    public void testInvalidateMetrics() {
        System.out.println("invalidateMetrics");

        FormulaMock formula = new FormulaMock();

        BaseElement instance = new BaseElementImpl();
        formula.add(instance);

        Cursor cursor = instance.getLast(drawer);

        formula.setInvalidated(0);

        instance.invalidateMetrics(null);

        assertEquals("Parent propogation", 1, formula.getInvalidated());
    }

//    /**
//     * Test of insertChar method, of class BaseElement.
//     */
//    @Test
//    public void testInsertChar() {
//        System.out.println("insertChar");
//        FormulaDrawer drawer = null;
//        CursorPosition cursor = null;
//        char c = ' ';
//        BaseElement instance = new BaseElementImpl();
//        CursorPosition expResult = null;
//        CursorPosition result = instance.insertChar(drawer, cursor, c);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    public class BaseElementImpl extends BaseElement {

        public boolean isComplex() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}