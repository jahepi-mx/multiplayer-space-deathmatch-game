package com.jahepi.tank.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;

/**
 * Created by jahepi on 28/06/16.
 */
public class ShipDialog extends Dialog {

    private static final String TAG = "ShipDialog";

    private Assets assets;
    private float ratio = 0.7f;
    private SelectBox<Option> ships;
    private Image imagePreview;
    private Label titleLabel, closeLabel, selectLabel;
    private TextureRegionDrawable ship1Image, ship2Image, ship3Image, ship4Image, ship5Image;
    private Option[] options;

    public ShipDialog() {
        super(Language.getInstance().get("ship_title"), Assets.getInstance().getSkin());
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
        titleLabel = new Label(Language.getInstance().get("ship_title"), titleStyle);
        getTitleTable().add(titleLabel).pad(10);

        closeLabel = new Label(Language.getInstance().get("close_btn"), titleStyle);
        Button closeBtn = new Button(assets.getSkin());
        closeBtn.add(closeLabel);
        getButtonTable().add(closeBtn).pad(30).uniform();

        Option ship1 = new Option(0, Language.getInstance().get("ship1_text"));
        Option ship2 = new Option(1, Language.getInstance().get("ship2_text"));
        Option ship3 = new Option(2, Language.getInstance().get("ship3_text"));
        Option ship4 = new Option(3, Language.getInstance().get("ship4_text"));
        Option ship5 = new Option(4, Language.getInstance().get("ship5_text"));
        options = new Option[] {
                ship1, ship2, ship3, ship4, ship5
        };

        ships = new SelectBox<Option>(assets.getSkin());
        ships.setItems(options);
        ships.setSelected(options[assets.getPlayer()]);
        ships.pack();

        ship1Image = new TextureRegionDrawable(assets.getShip1());
        ship2Image = new TextureRegionDrawable(assets.getShip2());
        ship3Image = new TextureRegionDrawable(assets.getShip3());
        ship4Image = new TextureRegionDrawable(assets.getShip4());
        ship5Image = new TextureRegionDrawable(assets.getShip5());

        imagePreview = new Image();
        imagePreview.setDrawable(getRegionsDrawable(assets.getPlayer()));

        selectLabel = new Label(Language.getInstance().get("select_ship_text"), titleStyle);

        ships.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log(TAG, ships.getSelected().getValue());
                assets.setPlayer(ships.getSelected().getIndex());
                imagePreview.setDrawable(getRegionsDrawable(assets.getPlayer()));
            }
        });

        getContentTable().clear();
        getContentTable().add(selectLabel).pad(5);
        getContentTable().add(ships).pad(5);
        getContentTable().row();
        getContentTable().add(imagePreview).colspan(2);
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

    public void updateTexts() {
        closeLabel.setText(Language.getInstance().get("close_btn"));
        titleLabel.setText(Language.getInstance().get("ship_title"));
        selectLabel.setText(Language.getInstance().get("select_ship_text"));
        options[0].setValue(Language.getInstance().get("ship1_text"));
        options[1].setValue(Language.getInstance().get("ship2_text"));
        options[2].setValue(Language.getInstance().get("ship3_text"));
        options[3].setValue(Language.getInstance().get("ship4_text"));
        options[4].setValue(Language.getInstance().get("ship5_text"));
        ships.pack();
    }

    public TextureRegionDrawable getRegionsDrawable(int index) {
        switch (index) {
            case 0: return ship1Image;
            case 1: return ship2Image;
            case 2: return ship3Image;
            case 3: return ship4Image;
            case 4: return ship5Image;
        }
        return ship1Image;
    }
}
