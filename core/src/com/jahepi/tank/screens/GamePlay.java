package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Render;
import com.jahepi.tank.TankField;
import com.jahepi.tank.multiplayer.dto.GameState;

public class GamePlay implements Screen {

	private static final String TAG = "GamePlay";
	
	private TankField tankField;
	private Render render;
	
	public GamePlay(TankField tankField) {
		this.tankField = tankField;
		render = new Render(this.tankField, this.tankField);
	}
	
	public void updateGameState(GameState gameState) {
		render.updateGameState(gameState);
	}
	
	public void removeOpponent(String id) {
		render.removeOpponent(id);
	}

	@Override
	public void show() {
		Assets.getInstance().getActionMusic().play();
	}
	
	public void showDisconnectError() {
		Gdx.app.log(TAG, "onDisconnect");
		render.showDisconnectError();
	}

	@Override
	public void render(float delta) {
		render.render();
	}

	@Override
	public void resize(int width, int height) {
		render.resize(width, height);	
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		Assets.getInstance().getActionMusic().stop();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		render.dispose();
	}
}
