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
    private Tile[] tiles;
    private Tile[] surroundedTiles;

    public Level(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        tileWidth = Config.WIDTH / this.cols;
        tileHeight = tileWidth;
        tileMap = new Array<Tile>();
        tiles = new Tile[cols * rows];
        surroundedTiles = new Tile[9];
    }

    public Tile getTile(int x, int y) {
        int position = (y * cols) + x;
        if (position >= 0 && position < tiles.length) {
            return tiles[position];
        }
        return null;
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

    public Tile[] getSurroundedTiles(float x, float y) {
        int xTile = (int) (x / tileWidth);
        int yTile = (int) (y / tileHeight);
        yTile = (rows - 1) - yTile;
        Tile centerTile = getTile(xTile, yTile);
        Tile leftTile = getTile(xTile - 1, yTile);
        Tile leftTopTile = getTile(xTile - 1, yTile + 1);
        Tile leftBottomTile = getTile(xTile - 1, yTile - 1);
        Tile rightTile = getTile(xTile + 1, yTile);
        Tile rightTopTile = getTile(xTile + 1, yTile + 1);
        Tile rightBottomTile = getTile(xTile + 1, yTile - 1);
        Tile topTile = getTile(xTile, yTile + 1);
        Tile bottomTile = getTile(xTile, yTile - 1);
        surroundedTiles[0] = centerTile;
        surroundedTiles[1] = leftTile;
        surroundedTiles[2] = rightTile;
        surroundedTiles[3] = topTile;
        surroundedTiles[4] = bottomTile;
        surroundedTiles[5] = leftTopTile;
        surroundedTiles[6] = leftBottomTile;
        surroundedTiles[7] = rightTopTile;
        surroundedTiles[8] = rightBottomTile;
        return surroundedTiles;
    }

    public void setMap(byte[] map) {
        this.map = map;
        for (int i = 0; i < this.map.length; i++) {
            if (this.map[i] == 1) {
                int y = i / cols;
                int x = i % cols;
                Tile tile = new Tile(tileWidth, tileHeight, x, (rows - 1) - y, Assets.getInstance().getAsteroid());
                tile.setOrigPosition(x, y);
                tileMap.add(tile);
                tiles[i] = tile;
            }
        }
    }
}
