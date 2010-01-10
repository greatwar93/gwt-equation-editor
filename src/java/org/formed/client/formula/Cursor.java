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
public final class Cursor {

    private FormulaDrawer drawer;
    private FormulaItem item;
    private int position;
    private int x;
    private int y;
    private int heightUp;
    private int heightDown;

    public Cursor(FormulaDrawer drawer, FormulaItem item, int position, int x, int y, int heightUp, int heightDown) {
        this.drawer = drawer;
        this.item = item;
        this.position = position;
        this.x = x;
        this.y = y;
        this.heightUp = heightUp;
        this.heightDown = heightDown;
    }

    public void setCursor(Cursor cursor){
        drawer = cursor.drawer;
        item = cursor.item;
        position = cursor.position;
        x = cursor.x;
        y = cursor.y;
        heightUp = cursor.heightUp;
        heightDown = cursor.heightDown;
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

    public Cursor moveRight(){
        setCursor(item.getRight(this.drawer, position));
        return this;
    }

    public Cursor moveLeft(){
        setCursor(item.getLeft(this.drawer, position));
        return this;
    }

    public Cursor moveUp(){
        setCursor(item.getUp(this.drawer, position));
        return this;
    }

    public Cursor moveDown(){
        setCursor(item.getDown(this.drawer, position));
        return this;
    }
}
