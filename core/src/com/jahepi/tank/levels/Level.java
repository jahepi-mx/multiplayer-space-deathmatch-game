package com.jahepi.tank.levels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by jahepi on 26/05/16.
 */
public class Level {

    private float width;
    private float height;
    private int cols;
    private int rows;
    private float tileSize;
    private TextureRegion background;
    private byte[] map;

    public Level(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        map = new byte[cols * rows];
    }

    public void setTile(int x, int y) {
        map[(x * cols) + y] = 1;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getTileSize() {
        return tileSize;
    }

    public void setTileSize(float tileSize) {
        this.tileSize = tileSize;
    }

    public TextureRegion getBackground() {
        return background;
    }

    public void setBackground(TextureRegion background) {
        this.background = background;
    }
}
