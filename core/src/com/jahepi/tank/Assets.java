package com.jahepi.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {

	private static Assets self;
	
	private AssetManager manager;
	private TextureAtlas atlas;
	private ParticleEffect effect2;
	private ParticleEffect effect3;
	private Sound audio1;
	private Sound audio2;
	private Sound audioItem;
	private Sound audioSpeedUp;
	private Sound destroySound;
	private Music music, action;
	private FreeTypeFontGenerator fontGenerator;
	private BitmapFont UIFontMain, UIFont, UIFontSmall, UIFontTitle, UIFontExtraSmall, UIFontOpponent, UIFontExtraExtraSmall;
	private ShaderProgram monochromeShader;
	private Skin skin;
	private Preferences preferences;
	private ParticleEffectPool effectPool;
	private ParticleEffectPool effectBigPool;
	private TextureRegion mainBackground, ship1, ship2, ship3, ship4, ship5, skull, rocket1, rocket2, rocket3, rocket4, rocket5, rocket6, rocket7, nuke, energy, shield, health, freeze, controlField, controlStick, button, button2, laser, life1, life2;

	private String[] nicknames = {"Taco", "Burrito", "Chilakil", "Burgerman", "Ponnio", "Panucho"};

	private Assets() {
		manager = new AssetManager();
		manager.load("images/multiplayer.pack", TextureAtlas.class);
		manager.finishLoading();
		atlas = manager.get("images/multiplayer.pack");
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		preferences = Gdx.app.getPreferences("game_preferences");
		
		if (!preferences.contains("music")) {
			preferences.putFloat("music", 0.3f);
		}
		if (!preferences.contains("effects")) {
			preferences.putFloat("effects", 1.0f);
		}
		if (!preferences.contains("port")) {
			preferences.putInteger("port", 38000);
		}
		if (!preferences.contains("name")) {
			preferences.putString("name", nicknames[MathUtils.random(0, nicknames.length - 1)]);
		}
		if (!preferences.contains("ms")) {
			preferences.putInteger("ms", 1000);
		}
		if (!preferences.contains("language")) {
			preferences.putString("language", Language.ENGLISH);
		}
		if (!preferences.contains("player")) {
			preferences.putInteger("player", MathUtils.random(0, 4));
		}
		if (!preferences.contains("map")) {
			preferences.putInteger("map", 2);
		}
		preferences.flush();

		effect2 = new ParticleEffect();
		effect2.load(Gdx.files.internal("particles/effect3.pfx"), atlas);
		effect2.scaleEffect(Config.MIN_EXPLOSION_SIZE);
		effect2.setEmittersCleanUpBlendFunction(false);

		effect3 = new ParticleEffect();
		effect3.load(Gdx.files.internal("particles/effect3.pfx"), atlas);
		effect3.scaleEffect(Config.MAX_EXPLOSION_SIZE);
		effect3.setEmittersCleanUpBlendFunction(false);

		audio1 = Gdx.audio.newSound(Gdx.files.internal("audio/laser1.mp3"));
		audio2 = Gdx.audio.newSound(Gdx.files.internal("audio/laser2.mp3"));
		audioItem = Gdx.audio.newSound(Gdx.files.internal("audio/powerup.mp3"));
		destroySound = Gdx.audio.newSound(Gdx.files.internal("audio/explosion.wav"));
		audioSpeedUp = Gdx.audio.newSound(Gdx.files.internal("audio/speedup.wav"));
		music = Gdx.audio.newMusic(Gdx.files.internal("audio/music.mp3"));
		music.setVolume(getMusicVolume());
		music.setLooping(true);
		action = Gdx.audio.newMusic(Gdx.files.internal("audio/action.ogg"));
		action.setVolume(getMusicVolume());
		action.setLooping(true);
		
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/game.ttf"));
		
		FreeTypeFontParameter parameters1 = new FreeTypeFontParameter();
	    parameters1.size = 55;
	    parameters1.shadowOffsetX = 1;
	    parameters1.shadowOffsetY = 1;
	    parameters1.color = Color.WHITE;
	    UIFont = fontGenerator.generateFont(parameters1);
	    
	    FreeTypeFontParameter parameters2 = new FreeTypeFontParameter();
	    parameters2.size = 50;
	    parameters2.shadowOffsetX = 1;
	    parameters2.shadowOffsetY = 1;
	    parameters2.color = Color.WHITE;
	    UIFontSmall = fontGenerator.generateFont(parameters2);
	    
	    FreeTypeFontParameter parameters3 = new FreeTypeFontParameter();
	    parameters3.size = 80;
	    parameters3.shadowOffsetX = 1;
	    parameters3.shadowOffsetY = 1;
	    parameters3.color = Color.GREEN;
	    parameters3.borderWidth = 2;
	    parameters3.borderColor = Color.GRAY;
	    UIFontTitle = fontGenerator.generateFont(parameters3);
	    
	    FreeTypeFontParameter parameters4 = new FreeTypeFontParameter();
	    parameters4.size = 40;
	    parameters4.shadowOffsetX = 1;
	    parameters4.shadowOffsetY = 1;
	    parameters4.color = Color.WHITE;
	    UIFontExtraSmall = fontGenerator.generateFont(parameters4);
	    
	    FreeTypeFontParameter parameters5 = new FreeTypeFontParameter();
	    parameters5.size = 80;
	    parameters5.shadowOffsetX = 1;
	    parameters5.shadowOffsetY = 1;
	    parameters5.color = Color.ORANGE;
	    parameters5.borderWidth = 2;
	    parameters5.borderColor = Color.WHITE;
	    UIFontMain = fontGenerator.generateFont(parameters5);

		FreeTypeFontParameter parameters6 = new FreeTypeFontParameter();
		parameters6.size = 80;
		parameters6.shadowOffsetX = 1;
		parameters6.shadowOffsetY = 1;
		parameters6.color = Color.RED;
		parameters6.borderWidth = 2;
		parameters6.borderColor = Color.WHITE;
		UIFontOpponent = fontGenerator.generateFont(parameters6);

		FreeTypeFontParameter parameters7 = new FreeTypeFontParameter();
		parameters7.size = 25;
		parameters7.shadowOffsetX = 1;
		parameters7.shadowOffsetY = 1;
		parameters7.color = Color.WHITE;
		UIFontExtraExtraSmall = fontGenerator.generateFont(parameters7);
	    
	    monochromeShader = new ShaderProgram(Gdx.files.internal("shader/monochrome.vs"), Gdx.files.internal("shader/monochrome.fs"));
	    monochromeShader.setUniformf("u_amount", 1.0f);

		effectPool = new ParticleEffectPool(effect2, 10, 20);
		effectBigPool = new ParticleEffectPool(effect3, 10, 20);

		mainBackground = atlas.findRegion("background", 6);
		ship1 = atlas.findRegion("ship1");
		ship2 = atlas.findRegion("ship2");
		ship3 = atlas.findRegion("ship3");
		ship4 = atlas.findRegion("ship4");
		ship5 = atlas.findRegion("ship5");
		skull = atlas.findRegion("skull");
		rocket1 = atlas.findRegion("rocket1");
		rocket2 = atlas.findRegion("rocket2");
		rocket3 = atlas.findRegion("rocket3");
		rocket4 = atlas.findRegion("rocket4");
		rocket5 = atlas.findRegion("rocket5");
		rocket6 = atlas.findRegion("rocket6");
		rocket7 = atlas.findRegion("rocket7");
		nuke = atlas.findRegion("nuke");
		energy = atlas.findRegion("energy");
		shield = atlas.findRegion("shield");
		health = atlas.findRegion("health");
		freeze = atlas.findRegion("freeze");
		controlField = atlas.findRegion("control_field");
		controlStick = atlas.findRegion("control_stick");
		button = atlas.findRegion("button");
		button2 = atlas.findRegion("button2");
		laser = atlas.findRegion("laser");
		life1 = atlas.findRegion("life1");
		life2 = atlas.findRegion("life2");

	}
	
	public static Assets getInstance() {
		if (self == null) {
			self = new Assets();
		}
		return self;
	}

	public TextureRegion getMainBackground() {
		return mainBackground;
	}

	public TextureRegion getShip1() {
		return ship1;
	}

	public TextureRegion getShip2() {
		return ship2;
	}
	
	public TextureRegion getShip3() {
		return ship3;
	}
	
	public TextureRegion getShip4() {
		return ship4;
	}
	
	public TextureRegion getShip5() {
		return ship5;
	}
	
	public TextureRegion getSkull() {
		return skull;
	}

	public TextureRegion getRocket1() {
		return rocket1;
	}

	public TextureRegion getRocket2() {
		return rocket2;
	}
	
	public TextureRegion getRocket3() {
		return rocket3;
	}
	
	public TextureRegion getRocket4() {
		return rocket4;
	}
	
	public TextureRegion getRocket5() {
		return rocket5;
	}
	
	public TextureRegion getRocket6() {
		return rocket6;
	}

	public TextureRegion getRocket7() {
		return rocket7;
	}
	
	public ParticleEffect getEffect2() {
		return effect2;
	}

	public Sound getAudio1() {
		return audio1;
	}
	
	public void playAudio1() {
		audio1.play(getEffectsVolume());
	}

	public void playAudio2() {
		audio2.play(getEffectsVolume());
	}
	
	public void playAudioItem() {
		audioItem.play(getEffectsVolume());
	}

	public void playDestroySound() {
		destroySound.play(getEffectsVolume());
	}

	public void playAudioSpeedUp() {
		audioSpeedUp.play(getEffectsVolume());
	}

	public Music getMusic() {
		return music;
	}
	
	public void playMusic() {
		music.play();
	}
	
	public void stopMusic() {
		music.stop();
	}
	
	public Music getActionMusic() {
		return action;
	}
	
	public void playActionMusic() {
		action.play();
	}
	
	public void stopActionMusic() {
		action.stop();
	}

	public BitmapFont getUIFont() {
		return UIFont;
	}

	public BitmapFont getUIFontSmall() {
		return UIFontSmall;
	}
	
	public BitmapFont getUIFontTitle() {
		return UIFontTitle;
	}
	
	public BitmapFont getUIFontOpponent() {
		return UIFontOpponent;
	}

	public BitmapFont getUIFontExtraSmall() {
		return UIFontExtraSmall;
	}

	public BitmapFont getUIFontExtraExtraSmall() {
		return UIFontExtraExtraSmall;
	}

	public BitmapFont getUIFontMain() {
		return UIFontMain;
	}

	public TextureRegion getNukeItem() {
		return nuke;
	}
	
	public TextureRegion getEneryItem() {
		return energy;
	}
	
	public TextureRegion getShieldItem() {
		return shield;
	}
	
	public TextureRegion getHealthItem() {
		return health;
	}
	
	public TextureRegion getFreezeItem() {
		return freeze;
	}

	public TextureRegion getControlField() {
		return controlField;
	}

	public TextureRegion getControlStick() {
		return controlStick;
	}

	public TextureRegion getButton1() {
		return button;
	}
	
	public TextureRegion getButton2() {
		return button2;
	}
	
	public TextureRegion getLaser() {
		return laser;
	}

	public TextureRegion getLife1() {
		return life1;
	}

	public TextureRegion getLife2() {
		return life2;
	}

	public TextureRegion getAsteroid() {
		int i = MathUtils.random(1, 5);
		return atlas.findRegion("asteroid", i);
	}

	public TextureRegion getBackground(int index) {
		return atlas.findRegion("background", index);
	}

	public ShaderProgram getMonochromeShader() {
		return monochromeShader;
	}
	
	public Animation getSpeedUpAnimation() {
		return new Animation(1.0f/12.0f, atlas.findRegions("OrangeBulletExplo"), Animation.PlayMode.LOOP);
	}

	public Animation getMegaShootAnimation() {
		return new Animation(1.0f/12.0f, atlas.findRegions("BlueBulletExplo"), Animation.PlayMode.LOOP);
	}

	public Skin getSkin() {
		return skin;
	}
	
	public String getNickname() {
		return preferences.getString("name");
	}
	
	public void setNickname(String name) {
		preferences.putString("name", name);
		preferences.flush();
	}
	
	public int getPort() {
		return preferences.getInteger("port");
	}
	
	public void setPort(int port) {
		preferences.putInteger("port", port);
		preferences.flush();
	}
	
	public int getMs() {
		return preferences.getInteger("ms");
	}
	
	public void setMs(int ms) {
		preferences.putInteger("ms", ms);
		preferences.flush();
	}
	
	public float getMusicVolume() {
		return preferences.getFloat("music");
	}
	
	public void setMusicVolume(float volume) {
		preferences.putFloat("music", volume);
		preferences.flush();
	}
	
	public float getEffectsVolume() {
		return preferences.getFloat("effects");
	}
	
	public void setEffectsVolume(float volume) {
		preferences.putFloat("effects", volume);
		preferences.flush();
	}
	
	public void setLanguage(String language) {
		preferences.putString("language", language);
		preferences.flush();
	}
	
	public String getLanguage() {
		return preferences.getString("language");
	}

	public void setMap(int index) {
		preferences.putInteger("map", index);
		preferences.flush();
	}

	public int getMap() {
		return preferences.getInteger("map");
	}

	public void setPlayer(int index) {
		preferences.putInteger("player", index);
		preferences.flush();
	}

	public int getPlayer() {
		return preferences.getInteger("player");
	}

	public ParticleEffect getParticleEffect() {
		return effectPool.obtain();
	}

	public void freeParticleEffect(ParticleEffect effect) {
		effectPool.free((ParticleEffectPool.PooledEffect) effect);
	}

	public ParticleEffect getBigParticleEffect() {
		return effectBigPool.obtain();
	}

	public void freeBigParticleEffect(ParticleEffect effect) {
		effectBigPool.free((ParticleEffectPool.PooledEffect) effect);
	}

	@Override
	public void dispose() {
		self = null;
		monochromeShader.dispose();
		atlas.dispose();
		music.dispose();
		action.dispose();
		audio1.dispose();
		audio2.dispose();
		audioItem.dispose();
		audioSpeedUp.dispose();
		destroySound.dispose();
		fontGenerator.dispose();
		effect2.dispose();
		UIFont.dispose();
		UIFontSmall.dispose();
		UIFontTitle.dispose();
		UIFontMain.dispose();
		UIFontExtraSmall.dispose();
		UIFontOpponent.dispose();
		UIFontExtraExtraSmall.dispose();
		skin.dispose();
		manager.dispose();
	}
}
