package me.hydro.emulator;

import lombok.Data;
import me.hydro.emulator.handler.impl.JumpHandler;
import me.hydro.emulator.handler.impl.MoveEntityHandler;
import me.hydro.emulator.handler.impl.MoveEntityWithHeadingHandler;
import me.hydro.emulator.handler.impl.MoveFlyingHandler;
import me.hydro.emulator.object.MoveTag;
import me.hydro.emulator.object.TagData;
import me.hydro.emulator.object.input.DataSupplier;
import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.object.result.IterationResult;
import me.hydro.emulator.util.MojangConstants;
import me.hydro.emulator.util.Vector;
import me.hydro.emulator.util.mcp.AxisAlignedBB;
import me.hydro.emulator.util.mcp.MathHelper;

import java.util.HashSet;
import java.util.Set;

@Data
public class Emulator {

    private Motion motion = new Motion(0D, 0D, 0D, 0F, 0F);
    private IterationInput input = null;
    private double offset = 0;
    private float friction;
    private boolean inWeb;
    private AxisAlignedBB lastReportedBoundingBox;

    private Set<TagData> tags = new HashSet<>();

    private final DataSupplier DATA_SUPPLIER;

    private final int protocolVersion;
    private final JumpHandler JUMP_HANDLER = new JumpHandler();
    private final MoveFlyingHandler MOVE_FLYING_HANDLER = new MoveFlyingHandler();
    private final MoveEntityHandler MOVE_ENTITY_HANDLER = new MoveEntityHandler();
    private final MoveEntityWithHeadingHandler MOVE_ENTITY_WITH_HEADING_HANDLER = new MoveEntityWithHeadingHandler();

    public IterationResult runIteration(final IterationInput input) {
        final Motion motion = this.motion.clone();
        final Set<TagData> tags = new HashSet<>();

        float forward = input.getForward();
        float strafing = input.getStrafing();

        // Are they sneaking? Slow them down some
        if (input.isSneaking()) {
            // these values aren't quite right,
            // try and find out what's wrong :)
            forward *= 0.3F;
            strafing *= 0.3F;

            tags.add(new TagData(MoveTag.SNEAKING));
        }

        // Are they using an item? Slow them down a little more
        if (input.isUsingItem()) {
            forward *= 0.2F;
            strafing *= 0.2F;

            tags.add(new TagData(MoveTag.USING));
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

            tags.add(new TagData(MoveTag.HIT_SLOW));
        }

        applyResetConstant(motion);

        if (input.isJumping()) {
            iteration = JUMP_HANDLER.handle(iteration);
            tags.add(new TagData(MoveTag.JUMP));
        }

        iteration = MOVE_ENTITY_WITH_HEADING_HANDLER.handle(iteration);

        return new IterationResult(iteration.getOffset(), iteration, iteration.getPredicted(), iteration.getMotion(),
                iteration.getTags());
    }

    public IterationResult runTeleportIteration(final Vector vector) {
        final Motion motion = this.motion.clone();
        final Set<TagData> tags = new HashSet<>();

        final float forward = 0;
        final float strafing = 0;

        motion.setForward(forward);
        motion.setStrafing(strafing);

        IterationInput input = IterationInput.builder()
                .jumping(false)
                .forward(0)
                .strafing(0)
                .sprinting(false)
                .usingItem(false)
                .hitSlowdown(false)
                .aiMoveSpeed(1)
                .fastMathType(MathHelper.FastMathType.FAST_LEGACY)
                .sneaking(false)
                .ground(true)
                .to(vector)
                .yaw(0)
                .lastReportedBoundingBox(new AxisAlignedBB(vector, 0.6, 1.8))
                .waitingForTeleport(true)
                .build();

        if(input != null)
            input.setWaitingForTeleport(true);

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

        iteration.getTags().add(new TagData(MoveTag.TELEPORT));

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

    public boolean containsTag(MoveTag tag) {
        return tags.contains(tag.getTagData());
    }

    public void runPostActions(final IterationHolder iteration) {
        iteration.getPostEmulation().forEach(post -> post.accept(this));
    }
}
