package com.jahepi.tank.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;

/**
 * Created by jahepi on 28/06/16.
 */
public class ShipDialog extends Dialog {

    private Assets assets;
    private float ratio = 0.7f;
    private SelectBox<Option> ships;
    private Image imagePreview;

    public ShipDialog() {
        super(Language.getInstance().get("search_server_title"), Assets.getInstance().getSkin());
        assets = Assets.getInstance();
        setModal(true);

        setResizable(false);
        setMovable(false);
        hide();
        setVisible(false);
        setWidth(Config.UI_WIDTH * ratio);
        setHeight(Config.UI_HEIGHT * ratio);
        setX((Config.UI_WIDTH / 2) - (getWidth() / 2));
        setY((Config.UI_HEIGHT / 2) - (getHeight() / 2));

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = assets.getUIFontSmall();
        getTitleTable().clearChildren();
        Label titleLabel = new Label(Language.getInstance().get("search_server_title"), titleStyle);
        getTitleTable().add(titleLabel).pad(10);

        Label closeLabel = new Label(Language.getInstance().get("close_btn"), titleStyle);
        Button closeBtn = new Button(assets.getSkin());
        closeBtn.add(closeLabel);
        getButtonTable().add(closeBtn).pad(30).uniform();

        Option ship1 = new Option(0, "X");
        Option ship2 = new Option(0, "XX");
        Option ship3 = new Option(0, "XXX");
        Option[] options = new Option[] {
                ship1, ship2, ship3
        };

        ships = new SelectBox<Option>(assets.getSkin());
        ships.setItems(options);
        ships.setSelected(options[assets.getPlayer()]);
        ships.pack();

        imagePreview = new Image();
        imagePreview.setDrawable(new TextureRegionDrawable(assets.getShip1()));

        getContentTable().clear();
        getContentTable().add(ships).pad(5);
        getContentTable().row();
        getContentTable().add(imagePreview);
        getContentTable().row();

        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        setVisible(true);
        super.show(stage, Actions.fadeIn(0.5f));
        return this;
    }

    @Override
    public float getPrefHeight() {
        return Config.UI_HEIGHT * ratio;
    }

    @Override
    public float getPrefWidth() {
        return Config.UI_WIDTH * ratio;
    }
}
