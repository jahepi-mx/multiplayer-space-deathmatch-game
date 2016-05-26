package com.jahepi.tank.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.multiplayer.NetworkUtils;

import java.net.Inet4Address;

/**
 * Created by javier.hernandez on 25/05/2016.
 */
public class IpDialog extends Dialog {

    private Assets assets;
    private Label ipsLabel;
    private Label infoLabel;
    private float ratio = 0.7f;

    public IpDialog() {
        super(Language.getInstance().get("ip_btn"), Assets.getInstance().getSkin());
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

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = assets.getUIFont();

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = assets.getUIFontSmall();
        getTitleTable().clearChildren();
        Label titleLabel = new Label(Language.getInstance().get("ip_btn"), titleStyle);
        getTitleTable().add(titleLabel).pad(10);

        ipsLabel = new Label("", style);
        ipsLabel.setWrap(true);
        ipsLabel.setAlignment(Align.center);
        infoLabel = new Label("", style);
        infoLabel.setWrap(true);
        infoLabel.setAlignment(Align.center);

        getContentTable().add(infoLabel).width(Config.UI_WIDTH / 2).pad(10);
        getContentTable().row();
        getContentTable().add(ipsLabel).width(Config.UI_WIDTH).pad(10);

        Label closeLabel = new Label(Language.getInstance().get("close_btn"), style);
        Button closeBtn = new Button(assets.getSkin());
        closeBtn.add(closeLabel);
        getButtonTable().add(closeBtn).pad(30).uniform();

        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    @Override
    public float getPrefHeight() {
        return Config.UI_HEIGHT * ratio;
    }

    @Override
    public float getPrefWidth() {
        return Config.UI_WIDTH * ratio;
    }

    @Override
    public Dialog show(Stage stage) {
        setVisible(true);
        String ipsText = "";
        Array<Inet4Address> ips = NetworkUtils.getMyIps();
        for (Inet4Address ip : ips) {
            ipsText += ip.getHostAddress() + "\n";
        }
        if (ips.size == 0) {
            infoLabel.setText(Language.getInstance().get("ip_error"));
        } else {
            infoLabel.setText(Language.getInstance().get("ip_success"));
            ipsLabel.setText(ipsText);
        }
        super.show(stage, Actions.fadeIn(0.5f));
        return this;
    }
}
