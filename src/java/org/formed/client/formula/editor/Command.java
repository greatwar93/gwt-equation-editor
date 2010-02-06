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
package org.formed.client.formula.editor;

/**
 * Interface used in a "Command" pattern
 * @see Undoer
 * @author Bulat Sirazetdinov
 */
public interface Command {
    /**
     * Command object that is used to specify that ther's no command (instead of null)
     */
    public static final Command ZERO_COMMAND = new Command() {

        public Cursor execute() {
            return null;
        }

        public void undo() {
        }
    };

    /**
     * Execute command
     * @return Cursor object that points to the position where cursor should move to after command execution
     */
    public Cursor execute();

    /**
     * Undo command
     */
    public void undo();
}
