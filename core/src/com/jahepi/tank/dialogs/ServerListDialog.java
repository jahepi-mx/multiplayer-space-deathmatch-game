package com.jahepi.tank.dialogs;

import com.badlogic.gdx.graphics.Color;
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

import java.net.InetSocketAddress;

/**
 * Created by javier.hernandez on 31/05/2016.
 */
public class ServerListDialog extends Dialog {

    private Assets assets;
    private Label errorLabel;
    private float ratio = 0.7f;
    private ServerListDialogListener listener;

    public ServerListDialog(ServerListDialogListener listener, Assets assets) {
        super(Language.getInstance().get("search_server_title"), assets.getSkin());
        this.listener = listener;
        this.assets = assets;
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

        errorLabel = new Label("", titleStyle);
        errorLabel.setColor(Color.RED);

        Label closeLabel = new Label(Language.getInstance().get("close_btn"), titleStyle);
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

    public Dialog show(Array<InetSocketAddress> servers, Stage stage) {
        setVisible(true);
        getContentTable().clear();
        errorLabel.setText("");

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = assets.getUIFont();
        Label infoLabel = new Label(Language.getInstance().get("server_list_info"), style);
        infoLabel.setWrap(true);
        infoLabel.setAlignment(Align.center);

        getContentTable().add(infoLabel).width(Config.UI_WIDTH).pad(5);
        getContentTable().row();
        getContentTable().add(errorLabel);
        getContentTable().row();

        for (final InetSocketAddress server : servers) {
            getContentTable().row();
            Label serverLabel = new Label(String.format(Language.getInstance().get("server_item_btn"), server.getAddress().toString()), style);
            Button serverBtn = new Button(assets.getSkin());
            serverBtn.setColor(Color.CYAN);
            serverBtn.add(serverLabel);
            serverBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listener.onSelectServer(server);
                }
            });
            getContentTable().add(serverBtn);
        }
        super.show(stage, Actions.fadeIn(0.5f));
        return this;
    }

    public void setErrorLabel(String error) {
        this.errorLabel.setText(error);
    }

    public interface ServerListDialogListener {
        public void onSelectServer(InetSocketAddress address);
    }
}
