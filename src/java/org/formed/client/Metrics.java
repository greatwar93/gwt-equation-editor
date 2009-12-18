/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formed.client;

/**
 *
 * @author bulats
 */
public final class Metrics {

    private int width = 0;
    private int heightUp = 0;
    private int heightDown = 0;

    public Metrics(int width, int heightUp, int heightDown) {
        this.width = width;
        this.heightUp = heightUp;
        this.heightDown = heightDown;
    }

    public int getHeightDown() {
        return heightDown;
    }

    public void setHeightDown(int heightDown) {
        this.heightDown = heightDown;
    }

    public int getHeightUp() {
        return heightUp;
    }

    public void setHeightUp(int heightUp) {
        this.heightUp = heightUp;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.heightUp + this.heightDown;
    }
}
