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
    }

    @Override
    public void onLand(Emulator iteration) {
    }

    @Override
    public float getFriction() {
        return 0.8f;
    }
}
