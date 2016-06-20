package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.TankField;

/**
 * Created by javier.hernandez on 26/05/2016.
 */
public class Credits implements Screen {

    private Stage stage;
    private Assets assets;
    private TankField tankField;
    private SpriteBatch batch;
    private boolean flag;

    private float focalLength = 300;
    private Vector3 camera;
    private float xOffset = Config.UI_WIDTH * 0.2f;
    private float yOffset = Config.UI_HEIGHT * 0.7f;
    private Array<CreditText> texts;
    private Vector3 front;
    private float frontX;
    private float frontY;

    public Credits(TankField tankField) {
        this.tankField = tankField;
        batch = this.tankField.getBatch();
        StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
        stage = new Stage(viewport, batch);
        assets = Assets.getInstance();
        camera = new Vector3(0, 0, 0);
        texts = new Array<CreditText>();

        CreditText text0 = new CreditText();
        text0.position.add(0, 150, 0);
        text0.text = Language.getInstance().get("credits_testing");
        text0.font = assets.getUIFontOpponent();

        CreditText text1 = new CreditText();
        text1.position.add(0, 100, 0);
        text1.text = Language.getInstance().get("credits_testing_text");
        text1.font = assets.getUIFont();

        CreditText text2 = new CreditText();
        text2.position.add(0, -200, 300);
        text2.text = Language.getInstance().get("credits_skull");
        text2.font = assets.getUIFontOpponent();

        CreditText text3 = new CreditText();
        text3.position.add(0, -250, 300);
        text3.text = Language.getInstance().get("credits_skull_text");
        text3.font = assets.getUIFont();

        CreditText text4 = new CreditText();
        text4.position.add(0, -150, 600);
        text4.text = Language.getInstance().get("credits_coding");
        text4.font = assets.getUIFontOpponent();

        CreditText text5 = new CreditText();
        text5.position.add(0, -200, 600);
        text5.text = Language.getInstance().get("credits_coding_text");
        text5.font = assets.getUIFont();

        texts.add(text5);
        texts.add(text4);
        texts.add(text3);
        texts.add(text2);
        texts.add(text1);
        texts.add(text0);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        stage.clear();

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = assets.getUIFontTitle();
        Label titleLabel = new Label(Language.getInstance().get("credits_title"), titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWrap(true);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = assets.getUIFontTitle();
        Label backLabel = new Label(Language.getInstance().get("back_btn"), style);
        Button backButton = new Button(assets.getSkin());
        backButton.add(backLabel);
        backLabel.setColor(Color.RED);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tankField.changeScreen(TankField.SCREEN_TYPE.MAIN);
            }
        });

        Table table = new Table();
        table.setHeight(Config.UI_HEIGHT);
        table.align(Align.top);
        table.add(titleLabel).width(Config.UI_WIDTH * 0.9f).pad(70.0f);
        table.row().expandY();
        table.add(backButton).align(Align.bottom).pad(10.0f);
        table.setFillParent(true);
        table.getColor().a = 0;
        table.addAction(Actions.fadeIn(0.5f));

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (camera.z > 730) {
            flag = true;
        }

        if (flag) {
            camera.z -= camera.z * 0.05f;
        } else {
            camera.z += 30 * delta;
        }

        if (camera.z <= 1) {
            flag = false;
        }

        if (front != null) {
            frontX = (front.x - camera.x) * 0.05f;
            camera.x += frontX;
            frontY = (front.y - camera.y) * 0.05f;
            camera.y += frontY;
        }

        batch.begin();
        batch.setShader(null);
        batch.draw(assets.getMainBackground(), 0, 0, Config.UI_WIDTH, Config.UI_HEIGHT);
        front = null;
        for (CreditText text : texts) {
            if (text.position.z >= (camera.z - 150)) {
                front = text.position;
                float ratio = getFocalRatio(text.position);
                float x = xOffset + ((text.position.x - camera.x) * ratio);
                float y = yOffset + ((text.position.y - camera.y) * ratio);
                text.font.getData().setScale(ratio);
                Color color = assets.getUIFont().getColor();
                text.font.setColor(color.r, color.g, color.b, ratio);
                text.font.draw(batch, text.text, x, y);
            }
        }
        batch.end();

        assets.getUIFont().getData().setScale(1);
        assets.getUIFontOpponent().getData().setScale(1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    public float getFocalRatio(Vector3 vector) {
        return focalLength / (focalLength + (vector.z - camera.z));
    }

    @Override
    public void dispose() {
        stage.clear();
        stage = null;
    }

    static class CreditText {
        private Vector3 position = new Vector3();
        private String text;
        private BitmapFont font;
    }
}
