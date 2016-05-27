package com.jahepi.tank.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by javier.hernandez on 27/05/2016.
 */
public class Tile extends GameEntity {

    private TextureRegion background;

    public Tile(float width, float height, int x, int y, TextureRegion background) {
        this.background = background;
        size.set(width, height);
        position.set(x, y);
        rectangle.setVertices(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
        rectangle.setPosition(position.x * size.x, position.y * size.y);
        rectangle.setOrigin(size.x / 2, size.y / 2);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(background, position.x * size.x, position.y * size.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);
        renderer.polygon(rectangle.getTransformedVertices());
    }

    @Override
    public void update(float deltatime) {

    }
}
