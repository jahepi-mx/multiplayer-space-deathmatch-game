package com.jahepi.tank.levels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.entities.Tile;

/**
 * Created by jahepi on 26/05/16.
 */
public class Level {

    private float width;
    private float height;
    private int cols;
    private int rows;
    private float tileWidth;
    private float tileHeight;
    private TextureRegion background;
    private byte[] map;
    private Array<Tile> tileMap;

    public Level(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        tileWidth = Config.WIDTH / this.cols;
        tileHeight = Config.HEIGHT / this.rows;
        tileMap = new Array<Tile>();
    }

    /*
    public Tile getTile(int x, int y) {
        return tileMap[(x * cols) + y];
    }
    */

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

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public TextureRegion getBackground() {
        return background;
    }

    public void setBackground(TextureRegion background) {
        this.background = background;
    }

    public Array<Tile> getTileMap() {
        return tileMap;
    }

    public void setMap(byte[] map) {
        this.map = map;
        for (int i = 0; i < this.map.length; i++) {
            if (this.map[i] == 1) {
                int y = i / cols;
                int x = i % cols;
                tileMap.add(new Tile(tileWidth, tileHeight, x, (rows - 1) - y, Assets.getInstance().getAsteroid()));
            }
        }
    }
}
