package me.hydro.emulator.collision.impl;

import me.hydro.emulator.Emulator;
import me.hydro.emulator.collision.Block;
import me.hydro.emulator.collision.CollisionBlockState;
import me.hydro.emulator.object.MoveTag;
import me.hydro.emulator.object.TagData;

public class BlockSoulSand extends Block implements CollisionBlockState {

    @Override
    public void transform(Emulator iteration) {
        iteration.getMotion().multiplyX(0.4D);
        iteration.getMotion().multiplyY(0.4D);
        iteration.getMotion().multiplyZ(0.4D);
        iteration.getTags().add(new TagData(MoveTag.SOUL_SAND));
    }
}
