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
import org.formed.client.formula.Drawer;
import org.formed.client.formula.FormulaItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bulat Sirazetdinov
 */
public class SimpleElementTest {

    Drawer drawer;

    public SimpleElementTest() {
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
//     * Test of getName method, of class SimpleElement.
//     */
//    @Test
//    public void testGetName() {
//        System.out.println("getName");
//        SimpleElement instance = null;
//        String expResult = "";
//        String result = instance.getName();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setName method, of class SimpleElement.
//     */
//    @Test
//    public void testSetName() {
//        System.out.println("setName");
//        String name = "";
//        SimpleElement instance = null;
//        instance.setName(name);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of insertChar method, of class SimpleElement.
     */
    @Test
    public void testInsertChar() {

        FormulaMock formula = new FormulaMock();

        SimpleElement instance = new SimpleElement("a");
        formula.add(instance);

        Cursor cursor = instance.getLast(drawer);
        formula.setInvalidated(0);
        Cursor newCursor = instance.insertChar(drawer, cursor, 'c');

        assertEquals("Name change", "ac", instance.getName());
        assertEquals("Parent propogation", 1, formula.getInvalidated());
        assertEquals("Cursor move", cursor.getPosition() + 1, newCursor.getPosition());


        cursor = newCursor;
        formula.setInvalidated(0);
        newCursor = instance.insertChar(drawer, cursor, ' ');

        assertEquals("Name change", "ac ", instance.getName());
        assertEquals("Parent propogation", 1, formula.getInvalidated());
        assertEquals("Cursor move", cursor.getPosition() + 1, newCursor.getPosition());


        cursor = newCursor;
        formula.setInvalidated(0);
        newCursor = instance.insertChar(drawer, cursor, 'a');

        assertEquals("Name change", "ac a", instance.getName());
        assertEquals("Parent propogation", 1, formula.getInvalidated());
        assertEquals("Cursor move", cursor.getPosition() + 1, newCursor.getPosition());
    }
}
