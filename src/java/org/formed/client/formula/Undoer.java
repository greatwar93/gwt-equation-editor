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

/**
 * Interface used by objects implementing undo/redo functionality
 * @see Command
 * @author Bulat Sirazetdinov
 */
public interface Undoer {

    /**
     * Undoer object used instead of null
     */
    public static final Undoer ZERO_UNDOER = new Undoer() {

        public void add(Command command) {
        }

        public void clear() {
        }

        public void clearUndos() {
        }

        public void clearRedos() {
        }

        public int getUndoCount() {
            return 0;
        }

        public int getRedoCount() {
            return 0;
        }

        public void undo() {
        }

        public void redo() {
        }
    };

    /**
     * Add new command into undo queue
     * @param command command to add into undo queue
     */
    public void add(Command command);

    /**
     * Clear undo and redo queues
     */
    public void clear();

    /**
     * Clear undo queue
     */
    public void clearUndos();

    /**
     * Clear redo queue
     */
    public void clearRedos();

    /**
     * Return how many commands are available to undo
     * @return count of executed commands in undo queue
     */
    public int getUndoCount();

    /**
     * Return how many commands are available to redo
     * @return count of undone commands in redo queue
     */
    public int getRedoCount();

    /**
     * Undo last executed command (last in the undo queue, and add it to redo queue)
     */
    public void undo();

    /**
     * Redo last undone command (last in the redo queue, and add it to undo queue)
     */
    public void redo();
}
