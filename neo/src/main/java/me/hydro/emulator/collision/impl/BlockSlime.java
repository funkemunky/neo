package me.hydro.emulator.collision.impl;

import me.hydro.emulator.Emulator;
import me.hydro.emulator.collision.Block;
import me.hydro.emulator.collision.CollisionLandable;
import me.hydro.emulator.collision.FrictionModifier;
import me.hydro.emulator.collision.VerticalCollisionBlock;
import me.hydro.emulator.object.iteration.IterationHolder;

public class BlockSlime extends Block implements CollisionLandable, VerticalCollisionBlock, FrictionModifier {

    @Override
    public void transform(Emulator iteration) {
        if(Math.abs(iteration.getMotion().getMotionY()) < 0.1D && !iteration.getInput().isSneaking()) {
            double factor = 0.4D + Math.abs(iteration.getMotion().getMotionY()) * 0.2D;
            iteration.getMotion().multiplyX(factor);
            iteration.getMotion().multiplyZ(factor);
            iteration.getTags().add("slime");
        }
    }

    @Override
    public void onLand(Emulator iteration) {
        if(iteration.getInput().isSneaking()) {
            super.onLand(iteration);
        } else if(iteration.getMotion().getMotionY() < 0D) {
            iteration.getMotion().multiplyY(-1);
            iteration.getTags().add("slime-land (" + iteration.getMotion().getMotionY() + ")");
        }
    }

    @Override
    public float getFriction() {
        return 0.8f;
    }
}
