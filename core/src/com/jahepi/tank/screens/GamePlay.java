package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Render;
import com.jahepi.tank.TankField;
import com.jahepi.tank.multiplayer.dto.GameState;

public class GamePlay implements Screen {

	private static final String TAG = "GamePlay";
	
	private TankField tankField;
	private Render render;
	private Assets assets;
	
	public GamePlay(TankField tankField) {
		this.tankField = tankField;
		render = new Render(this.tankField, this.tankField);
		assets = tankField.getAssets();
	}
	
	public void updateGameState(GameState gameState) {
		render.updateGameState(gameState);
	}
	
	public void removeOpponent(String id) {
		render.removeOpponent(id);
	}

	@Override
	public void show() {
		assets.stopMusic();
		assets.playActionMusic();
	}
	
	public void showDisconnectError() {
		Gdx.app.log(TAG, "onDisconnect");
		render.showDisconnectError();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		render.render();
	}

	@Override
	public void resize(int width, int height) {
		render.resize(width, height);	
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		assets.stopActionMusic();
	}

	@Override
	public void dispose() {
		render.dispose();
	}
}
