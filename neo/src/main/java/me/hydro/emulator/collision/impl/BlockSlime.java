package me.hydro.emulator.collision.impl;

import me.hydro.emulator.Emulator;
import me.hydro.emulator.collision.Block;
import me.hydro.emulator.collision.CollisionLandable;
import me.hydro.emulator.collision.FrictionModifier;
import me.hydro.emulator.collision.VerticalCollisionBlock;
import me.hydro.emulator.object.InformationData;
import me.hydro.emulator.object.MoveTag;
import me.hydro.emulator.object.TagData;

public class BlockSlime extends Block implements CollisionLandable, VerticalCollisionBlock, FrictionModifier {

    @Override
    public void transform(Emulator iteration) {
        if(Math.abs(iteration.getMotion().getMotionY()) < 0.1D && !iteration.getInput().isSneaking()) {
            double factor = 0.4D + Math.abs(iteration.getMotion().getMotionY()) * 0.2D;
            iteration.getMotion().multiplyX(factor);
            iteration.getMotion().multiplyZ(factor);
            iteration.getTags().add(new TagData(MoveTag.SLIME));
        }
    }

    @Override
    public void onLand(Emulator iteration) {
        if(iteration.getInput().isSneaking()) {
            super.onLand(iteration);
        } else if(iteration.getMotion().getMotionY() < 0D) {
            iteration.getMotion().multiplyY(-1);
            iteration.getTags().add(new InformationData(MoveTag.SLIME_LANDED, String.valueOf(iteration.getMotion().getMotionY())));
        }
    }

    @Override
    public float getFriction() {
        return 0.8f;
    }
}
