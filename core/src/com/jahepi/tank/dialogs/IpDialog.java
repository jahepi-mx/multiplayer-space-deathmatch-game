package com.jahepi.tank.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;

/**
 * Created by javier.hernandez on 25/05/2016.
 */
public class IpDialog extends Dialog {

    private Assets assets;
    private Label info;

    public IpDialog() {
        super(Language.getInstance().get("ip_btn"), Assets.getInstance().getSkin());
        assets = Assets.getInstance();
        setModal(true);
        setVisible(false);
        setResizable(false);
        setMovable(false);
        setWidth(Config.UI_WIDTH / 2);
        setHeight(Config.UI_HEIGHT / 2);
        setX((Config.UI_WIDTH / 2) - (getWidth() / 2));
        setY((Config.UI_HEIGHT / 2) - (getHeight() / 2));

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont uiFont = assets.getUIFontMain();
        style.font = uiFont;

        info = new Label("", style);

        getContentTable().add(info).pad(30).uniform();

        TextButton closeBtn = new TextButton("close", assets.getSkin());
        getButtonTable().add(closeBtn).pad(30).uniform();

        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancel();
                hide();
                remove();
            }
        });
    }

    @Override
    public float getPrefHeight() {
        return Config.UI_HEIGHT / 2;
    }

    @Override
    public float getPrefWidth() {
        return Config.UI_WIDTH / 2;
    }

    public void setText(String content) {
        info.setText(content);
    }

    @Override
    public Dialog show(Stage stage) {
        setVisible(true);
        super.show(stage, Actions.fadeIn(0.5f));
        return this;
    }
}
