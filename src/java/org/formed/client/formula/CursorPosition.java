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

/**
 *
 * @author bulats
 */
public final class CursorPosition {

    private FormulaItem item;
    private int position;
    private int x;
    private int y;
    private int heightUp;
    private int heightDown;

    public CursorPosition(FormulaItem item, int position, int x, int y, int heightUp, int heightDown) {
        this.item = item;
        this.position = position;
        this.x = x;
        this.y = y;
        this.heightUp = heightUp;
        this.heightDown = heightDown;
    }

    public int getHeightDown() {
        return heightDown;
    }

    public int getHeightUp() {
        return heightUp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosition() {
        return position;
    }

    public FormulaItem getItem() {
        return item;
    }
}
