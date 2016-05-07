package com.jahepi.tank.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class GameEntity {

	protected String id;
	protected Vector2 position;
	protected Vector2 size;
	protected Polygon rectangle;
	protected float rotation;
	protected float speed;
	protected float velocity;
	protected float rotationSpeed;
	protected boolean removed;
	
	public GameEntity() {
		id = "default";
		position = new Vector2();
		size = new Vector2();
		rectangle = new Polygon();
	}
	
	public abstract void render(SpriteBatch batch);
	
	public abstract void debugRender(ShapeRenderer renderer);
	
	public abstract void update(float deltatime);
	
	public boolean collide(Polygon rectangle) {
		return Intersector.overlapConvexPolygons(this.rectangle, rectangle);
	}
	
	public float distance(float x, float y) {
		return position.dst(x, y);
	}
	
	public Polygon getRectangle() {
		return rectangle;
	}
	
	public void setX(float x) {
		position.x = x;
	}
	
	public float getX() {
		return position.x;
	}
	
	public void setY(float y) {
		position.y = y;
	}
	
	public float getY() {
		return position.y;
	}
	
	public void setWidth(float width) {
		size.x = width;
	}
	
	public float getWidth() {
		return size.x;
	}
	
	public void setHeight(float height) {
		size.y = height;
	}
	
	public float getHeight() {
		return size.y;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public float getRotation() {
		return rotation;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}
