package com.jahepi.tank.levels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;

/**
 * Created by jahepi on 26/05/16.
 */
public class LevelFactory {

    private Array<Level> levels;
    private int selectedLevel;

    public LevelFactory() {
        levels = new Array<Level>();
        Level level = new Level(20, 10);
        level.setWidth(21.33f * 4);
        level.setHeight(16 * 4);
        level.setBackground(Assets.getInstance().getMainBackground());
        level.setTileSize(100);

        Level level2 = new Level(20, 10);
        level2.setWidth(21.33f * 6);
        level2.setHeight(16 * 6);
        level2.setBackground(Assets.getInstance().getBackground());
        level2.setTileSize(100);

        levels.add(level);
        levels.add(level2);
    }

    public Level getRandomLevel() {
        selectedLevel = MathUtils.random(0, levels.size - 1);
        return levels.get(selectedLevel);
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public Level getLevel(int index) {
        selectedLevel = index;
        return levels.get(selectedLevel);
    }
}
