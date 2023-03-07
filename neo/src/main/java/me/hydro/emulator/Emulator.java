package me.hydro.emulator;

import lombok.Data;
import me.hydro.emulator.handler.impl.JumpHandler;
import me.hydro.emulator.handler.impl.MoveEntityHandler;
import me.hydro.emulator.handler.impl.MoveEntityWithHeadingHandler;
import me.hydro.emulator.handler.impl.MoveFlyingHandler;
import me.hydro.emulator.object.input.DataSupplier;
import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.object.result.IterationResult;
import me.hydro.emulator.util.MojangConstants;
import me.hydro.emulator.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Emulator {

    private Motion motion = new Motion(0D, 0D, 0D, 0F, 0F);
    private IterationInput input = null;
    private double offset = 0;
    private float friction;
    private boolean inWeb;

    private List<String> tags = Collections.emptyList();

    private final DataSupplier DATA_SUPPLIER;

    private final int protocolVersion;
    private final JumpHandler JUMP_HANDLER = new JumpHandler();
    private final MoveFlyingHandler MOVE_FLYING_HANDLER = new MoveFlyingHandler();
    private final MoveEntityHandler MOVE_ENTITY_HANDLER = new MoveEntityHandler();
    private final MoveEntityWithHeadingHandler MOVE_ENTITY_WITH_HEADING_HANDLER = new MoveEntityWithHeadingHandler();

    public IterationResult runIteration(final IterationInput input) {
        final Motion motion = this.motion.clone();
        final List<String> tags = new ArrayList<>();

        float forward = input.getForward();
        float strafing = input.getStrafing();

        // Are they sneaking? Slow them down some
        if (input.isSneaking()) {
            // these values aren't quite right,
            // try and find out what's wrong :)
            forward *= 0.3F;
            strafing *= 0.3F;

            tags.add("sneaking");
        }

        // Are they using an item? Slow them down a little more
        if (input.isUsingItem()) {
            forward *= 0.2F;
            strafing *= 0.2F;

            tags.add("using");
        }

        // Mojang multiplies by 0.98F, so do we
        forward *= 0.9800000190734863F;
        strafing *= 0.9800000190734863F;

        motion.setForward(forward);
        motion.setStrafing(strafing);

        // Create the new iteration holder
        IterationHolder iteration = new IterationHolder(this, input, DATA_SUPPLIER);

        // Setting to previous motion calculated

        iteration.setMotion(motion);
        iteration.setTags(tags);

        // Hit slowdown modifies motion and not forward/strafing input
        // Multiply by 0.6D
        if (input.isHitSlowdown()) {
            motion.multiplyX(0.6D);
            motion.multiplyZ(0.6D);

            tags.add("slowdown");
        }

        applyResetConstant(motion);

        if (input.isJumping()) {
            iteration = JUMP_HANDLER.handle(iteration);
            tags.add("jump");
        }

        iteration = MOVE_ENTITY_WITH_HEADING_HANDLER.handle(iteration);

        return new IterationResult(iteration.getOffset(), iteration, iteration.getPredicted(), iteration.getMotion(),
                iteration.getTags());
    }

    public IterationResult runTeleportIteration(final Vector vector) {
        final Motion motion = this.motion.clone();
        final List<String> tags = new ArrayList<>();

        final float forward = 0;
        final float strafing = 0;

        motion.setForward(forward);
        motion.setStrafing(strafing);

        tags.add("teleport");

        // Create the new iteration holder
        IterationHolder iteration = new IterationHolder(this, input, DATA_SUPPLIER);

        // Setting previous motion to 0
        motion.setMotionX(0);
        motion.setMotionY(0);
        motion.setMotionZ(0);

        iteration.setMotion(motion);
        iteration.setTags(tags);

        // Setting the reset constant
        applyResetConstant(motion);

        iteration.setPredicted(vector);
        iteration.setOffset(iteration.getInput().getTo().distance(iteration.getPredicted()));

        return new IterationResult(iteration.getOffset(), iteration, iteration.getPredicted(), iteration.getMotion(),
                iteration.getTags());
    }

    private void applyResetConstant(Motion motion) {
        final double RESET = protocolVersion > 47 ? MojangConstants.RESET : MojangConstants.RESET_LEGACY;

        if (Math.abs(motion.getMotionX()) < RESET) motion.setMotionX(0);
        if (Math.abs(motion.getMotionY()) < RESET) motion.setMotionY(0);
        if (Math.abs(motion.getMotionZ()) < RESET) motion.setMotionZ(0);
    }

    public void confirm(final IterationHolder iteration) {
        this.motion = iteration.getMotion();
        this.input = iteration.getInput();
        this.offset = iteration.getOffset();
        this.friction = iteration.getFriction();
        this.tags = iteration.getTags();

        runPostActions(iteration);
    }

    public void runPostActions(final IterationHolder iteration) {
        iteration.getPostEmulation().forEach(post -> post.accept(this));
    }
}
