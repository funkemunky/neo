package me.hydro.emulator.collision.impl;

import me.hydro.emulator.collision.Block;
import me.hydro.emulator.collision.FrictionModifier;

public class BlockIce extends Block implements FrictionModifier {
    @Override
    public float getFriction() {
        return 0.98f;
    }
}
