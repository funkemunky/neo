package me.hydro.emulator.handler.impl;

import me.hydro.emulator.handler.MovementHandler;
import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.util.MojangConstants;
import me.hydro.emulator.util.PotionEffect;

public class MoveEntityWithHeadingHandler implements MovementHandler {

    @Override
    public IterationHolder handle(IterationHolder iteration) {
        final IterationInput input = iteration.getInput();
        final boolean onGround = input.isGround();

        // Here we'll get the friction of the block below
        final float friction = onGround
                ? 0.6F * 0.91F // Blocks can have different friction :)
                : 0.91F;

        // Variable (currently unassigned) where we'll put our moveSpeed
        float moveSpeed;

        if (onGround) {
            // Here we'll calculate AI move speed
            // drag = 0.16277136 * friction^3
            //
            // EntityLivingBase#moveEntityWithHeading
            final double aiMoveSpeed = getAiMoveSpeed(input.getSpeed(), input.getSlowness(), input.isSprinting());
            final float drag = MojangConstants.LAND_MOVEMENT_FACTOR_LEGACY / (friction * friction * friction);

            // Set moveSpeed to aiMoveSpeed * drag
            moveSpeed = (float) (aiMoveSpeed * drag);
            iteration.getTags().add("ground");
        } else {
            // Found in EntityPlayer#onLivingUpdate (jumpMovementFactor of EntityLivingBase)
            // Set moveSpeed depending on sprint status
            // This isn't completely accurate :)
            moveSpeed = input.isSprinting() ? MojangConstants.SPEED_AIR_SPRINTING : MojangConstants.SPEED_AIR;
        }

        // Set friction to moveSpeed temporarily
        // This is how our move flying handler will access moveSpeed
        iteration.setFriction(moveSpeed);

        // Run Entity#moveFlying
        iteration = iteration.getEmulator().getMOVE_FLYING_HANDLER().handle(iteration);

        // Set friction back to the actual friction
        iteration.setFriction(friction);

        // Run Entity#moveEntity
        iteration = iteration.getEmulator().getMOVE_ENTITY_HANDLER().handle(iteration);

        iteration.addPostAction(emulator -> {
            if (emulator.getMotion() == null) return;

            // gravity and friction shiz
            emulator.getMotion().subtractY(0.08D);

            emulator.getMotion().multiplyY(MojangConstants.GRAVITY);
            emulator.getMotion().multiplyX(friction);
            emulator.getMotion().multiplyZ(friction);
        });

        return iteration;
    }

    private double getAiMoveSpeed(final PotionEffect speed, PotionEffect slowness, final boolean sprinting) {
        double aiMoveSpeed = 0.1F;

        if (sprinting) aiMoveSpeed += aiMoveSpeed * MojangConstants.SPRINT_MULTIPLIER;

        // I did it myself, don't be a dick Hydrogen.
        if(speed != null) {
             aiMoveSpeed += (speed.getAmplifier() + 1) * 0.20000000298023224D * aiMoveSpeed;
        }

        if(slowness != null) {
            aiMoveSpeed = (slowness.getAmplifier() + 1) * -0.15000000596046448D * aiMoveSpeed;
        }

        return aiMoveSpeed;
    }
}
