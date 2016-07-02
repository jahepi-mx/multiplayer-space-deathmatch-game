package com.jahepi.tank.mem;

import com.badlogic.gdx.utils.Disposable;
import com.jahepi.tank.TankField;
import com.jahepi.tank.multiplayer.dto.GameState;
import com.jahepi.tank.screens.GamePlay;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by jahepi on 02/07/16.
 */
public class RunnableManager implements Disposable {

    private ArrayBlockingQueue<Runnable> runnables;

    public RunnableManager(int size) {
        runnables = new ArrayBlockingQueue<Runnable>(size);
        for (int i = 0; i < size; i++) {
            runnables.add(new RunnableTask());
        }
    }

    public void add(RunnableTask runnableTask) {
        runnables.add(runnableTask);
    }

    public Runnable poll() {
        return runnables.poll();
    }

    public int getSize() {
        return runnables.size();
    }

    @Override
    public void dispose() {
        runnables.clear();
    }

    public class RunnableTask implements Runnable {

        private TankField tankField;
        private String data;

        @Override
        public void run() {
            if (tankField.getCurrentScreen() instanceof GamePlay) {
                GameState gameState = tankField.getJson().fromJson(GameState.class, data);
                ((GamePlay) tankField.getCurrentScreen()).updateGameState(gameState);
            }
            runnables.add(this);
        }

        public void setTankField(TankField tankField) {
            this.tankField = tankField;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
