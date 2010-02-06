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
package org.formed.client.formula;

import java.util.List;
import org.formed.client.formula.drawer.Metrics;
import org.formed.client.formula.editor.AutoCompletion;

/**
 * Interface used by editors
 * @author Bulat Sirazetdinov
 */
public interface Editor {

    public static final Editor ZERO_EDITOR = new Editor() {

        public Metrics redraw() {
            return new Metrics(0, 0, 0);
        }

        public void cut() {
        }

        public void copy() {
        }

        public void paste() {
        }

        public void insert(char c) {
        }

        public void insertElement(FormulaItem item) {
        }

        public void populateAutoNew(List<AutoCompletion> list) {
        }

        public void populateAutoSimple(List<AutoCompletion> list) {
        }

        public void populateAutoFunction(List<AutoCompletion> list) {
        }

        public void populateAutoOperator(List<AutoCompletion> list) {
        }

        public boolean moveCursorLeft() {
            return false;
        }

        public boolean moveCursorRight() {
            return false;
        }

        public boolean moveCursorUp() {
            return false;
        }

        public boolean moveCursorDown() {
            return false;
        }

        public boolean moveCursorFirst() {
            return false;
        }

        public boolean moveCursorLast() {
            return false;
        }

        public boolean moveCursorToPower() {
            return false;
        }

        public void selectLeft() {
        }

        public void selectRight() {
        }

        public void selectUp() {
        }

        public void selectDown() {
        }

        public void selectAll() {
        }

        public void deleteLeft() {
        }

        public void deleteRight() {
        }

        public boolean isAutoCompletion() {
            return false;
        }

        public void showAutoCompletion() {
        }

        public void hideAutoCompletion() {
        }

        public void moveAutoCompletionUp() {
        }

        public void moveAutoCompletionDown() {
        }

        public void selectAutoCompletion() {
        }

        public void mouseMoveAt(int x, int y) {
        }

        public void mouseDownAt(int x, int y) {
        }

        public void mouseUpAt(int x, int y) {
        }
    };

    Metrics redraw();

    public void cut();
    public void copy();
    public void paste();

    public void insert(char c);
    public void insertElement(FormulaItem item);

    public void populateAutoNew(List<AutoCompletion> list);
    public void populateAutoSimple(List<AutoCompletion> list);
    public void populateAutoFunction(List<AutoCompletion> list);
    public void populateAutoOperator(List<AutoCompletion> list);

    public boolean moveCursorLeft();
    public boolean moveCursorRight();
    public boolean moveCursorUp();
    public boolean moveCursorDown();
    public boolean moveCursorFirst();
    public boolean moveCursorLast();
    public boolean moveCursorToPower();

    public void selectLeft();
    public void selectRight();
    public void selectUp();
    public void selectDown();
    public void selectAll();

    public void deleteLeft();
    public void deleteRight();

    public boolean isAutoCompletion();
    public void showAutoCompletion();
    public void hideAutoCompletion();
    public void moveAutoCompletionUp();
    public void moveAutoCompletionDown();
    public void selectAutoCompletion();

    public void mouseMoveAt(int x, int y);
    public void mouseDownAt(int x, int y);
    public void mouseUpAt(int x, int y);
}
