package com.jahepi.tank.mem;

import com.badlogic.gdx.utils.Pool;
import com.jahepi.tank.multiplayer.dto.MissileState;
import com.jahepi.tank.multiplayer.dto.PowerUpState;
import com.jahepi.tank.multiplayer.dto.TankState;

/**
 * Created by jahepi on 03/07/16.
 */
public class GameStateManager {

    private final Pool<TankState> tankStatePool = new Pool<TankState>(4) {
        @Override
        protected TankState newObject() {
            return new TankState();
        }
    };

    private final Pool<MissileState> missileStatePool = new Pool<MissileState>() {
        @Override
        protected MissileState newObject() {
            return new MissileState();
        }
    };

    private final Pool<PowerUpState> powerUpStatePool = new Pool<PowerUpState>() {
        @Override
        protected PowerUpState newObject() {
            return new PowerUpState();
        }
    };

    public GameStateManager() {

    }

    public MissileState obtainMissileState() {
        return missileStatePool.obtain();
    }

    public PowerUpState obtainPowerUpState() {
        return powerUpStatePool.obtain();
    }

    public TankState obtainTankState() {
        return tankStatePool.obtain();
    }

    public void freeMissileState(MissileState missileState) {
        missileStatePool.free(missileState);
    }

    public void freePowerUpState(PowerUpState powerUpState) {
        powerUpStatePool.free(powerUpState);
    }

    public void freeTankState(TankState tankState) {
        tankStatePool.free(tankState);
    }
}
