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
public final class Metrics {

    private int width;
    private int heightUp;
    private int heightDown;

    public Metrics(int width, int heightUp, int heightDown) {
        this.width = width;
        this.heightUp = heightUp;
        this.heightDown = heightDown;
    }

    public Metrics(double width, double heightUp, double heightDown) {
        this.width = (int)width;
        this.heightUp = (int)heightUp;
        this.heightDown = (int)heightDown;
    }

    public Metrics cloneMetrics(){
        return new Metrics(width, heightUp, heightDown);
    }

    public void clear() {
        width = 0;
        heightUp = 0;
        heightDown = 0;
    }

    public void add(Metrics metrics) {
        width += metrics.getWidth();
        heightUp = Math.max(heightUp, metrics.getHeightUp());
        heightDown = Math.max(heightDown, metrics.getHeightDown());
    }

    public void add(Metrics metrics, int x, int y) {
        width += x + metrics.getWidth();
        heightUp = Math.max(heightUp, -y + metrics.getHeightUp());
        heightDown = Math.max(heightDown, y + metrics.getHeightDown());
    }

    public int getHeightDown() {
        return heightDown;
    }

    public int getHeightUp() {
        return heightUp;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return heightUp + heightDown;
    }

    public void setHeightDown(int heightDown) {
        this.heightDown = heightDown;
    }

    public void setHeightUp(int heightUp) {
        this.heightUp = heightUp;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
