package me.hydro.emulator.collision;

import me.hydro.emulator.Emulator;

public class Block implements CollisionLandable {
    @Override
    public void onLand(Emulator iteration) {
        iteration.getMotion().setMotionY(0);
    }
}
