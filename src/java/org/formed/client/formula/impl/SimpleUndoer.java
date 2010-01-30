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
package org.formed.client.formula.impl;

import java.util.ArrayList;
import java.util.List;
import org.formed.client.formula.Command;
import org.formed.client.formula.Undoer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class SimpleUndoer implements Undoer {

    private int maxSteps = 100;
    private List<Command> undos = new ArrayList<Command>();
    private List<Command> redos = new ArrayList<Command>();

    public SimpleUndoer() {
    }

    public SimpleUndoer(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    private void shrinkToMaxSteps(){
        while(undos.size() > maxSteps){
            undos.remove(0);
        }
    }

    public void add(Command command) {
        if (command == Command.ZERO_COMMAND) {
            return;
        }
        
        undos.add(command);
        redos.clear();

        shrinkToMaxSteps();
    }

    public void clear() {
        redos.clear();
        undos.clear();
    }

    public void clearUndos() {
        undos.clear();
    }

    public void clearRedos() {
        redos.clear();
    }

    public int getUndoCount() {
        return undos.size();
    }

    public int getRedoCount() {
        return redos.size();
    }

    public void undo() {
        if (undos.size() <= 0) {
            return;
        }
        Command command = undos.get(undos.size() - 1);
        redos.add(command);
        undos.remove(command);
        command.undo();
    }

    public void redo() {
        if (redos.size() <= 0) {
            return;
        }
        Command command = redos.get(redos.size() - 1);
        undos.add(command);
        redos.remove(command);
        command.execute();

        shrinkToMaxSteps();
    }
}
