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
package org.formed.client.formula.drawer;

/**
 *
 * @author Bulat Sirazetdinov
 */
public final class Rectangle {

    private int x;
    private int y;
    private int width;
    private int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isInside(int xCoord, int yCoord) {
//        return ((xCoord >= x) && (xCoord < x + width) && (yCoord >= y) && (yCoord < y + height));
        return ((xCoord >= x) && (xCoord <= x + width) && (yCoord >= y) && (yCoord <= y + height));
    }

    public boolean isSmaller(Rectangle rect) {
        if (rect == null) {
            return true;
        }
        return (width * height < rect.getWidth() * rect.getHeight());
    }
}
