package me.hydro.emulator;

import lombok.Data;
import me.hydro.emulator.handler.impl.*;
import me.hydro.emulator.object.input.DataSupplier;
import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.object.result.IterationResult;
import me.hydro.emulator.util.MojangConstants;
import me.hydro.emulator.util.Vec2;
import me.hydro.emulator.util.Vector;
import me.hydro.emulator.util.mcp.AxisAlignedBB;
import me.hydro.emulator.util.mcp.MathHelper;

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
    private AxisAlignedBB lastReportedBoundingBox;

    private List<String> tags = Collections.emptyList();

    private final DataSupplier DATA_SUPPLIER;

    private final int playerVersion;
    private final JumpHandler JUMP_HANDLER = new JumpHandler();
    private final MoveFlyingHandler MOVE_FLYING_HANDLER = new MoveFlyingHandler();
    private final ApplyMovementInputHandler APPLY_MOVEMENT_INPUT_HANDLER = new ApplyMovementInputHandler();
    private final MoveEntityHandler MOVE_ENTITY_HANDLER = new MoveEntityHandler();
    private final MoveEntityWithHeadingHandler MOVE_ENTITY_WITH_HEADING_HANDLER = new MoveEntityWithHeadingHandler();

    public IterationResult runIteration(final IterationInput input) {
        final Motion motion = this.motion.clone();
        final List<String> tags = new ArrayList<>();

        float forward;
        float strafing;

        if(input.isModernMovement()) {
            Vec2 resultingInput = getModernResultingInput(input, tags);
            forward = resultingInput.x();
            strafing = resultingInput.y();
        } else {
            Vec2 resultingInput = getResultingInput(input, tags);
            forward = resultingInput.x();
            strafing = resultingInput.y();
        }

        if(input.isSprinting()) {
            tags.add("sprinting");
        }


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
            var result = JUMP_HANDLER.handle(iteration);

            if(result == null) {
                System.out.println("There was a null point that was about to occur with JUMP");
            } else iteration = result;

            tags.add("jump");
        }

        var result = MOVE_ENTITY_WITH_HEADING_HANDLER.handle(iteration);

        if(result == null) {
            System.out.println("There was a null point that was about to occur with MOVE ENTITY");
        } else iteration = result;

        return new IterationResult(iteration.getOffset(), iteration, iteration.getPredicted(), iteration.getMotion(),
                iteration.getTags());
    }

    private Vec2 getResultingInput(IterationInput input, List<String> tags) {
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

        forward*= 0.98F;
        strafing*= 0.98F;

        return new Vec2(forward, strafing);
    }

    private Vec2 getModernResultingInput(IterationInput input, List<String> tags) {
        Vec2 moveVector = new Vec2(input.getForward(), input.getStrafing()).normalized();

        if(moveVector.lengthSquared() == 0) {
            return moveVector;
        }

        moveVector = moveVector.scale(0.98F);

        if(input.isUsingItem()) {
            tags.add("using");
            moveVector = moveVector.scale(0.2F);
        }
        if(input.isSneaking()) {
            tags.add("sneaking");
            moveVector = moveVector.scale(0.3F);
        }

        return modifyInputSpeedForSquareMovement(moveVector);
    }

    private static Vec2 modifyInputSpeedForSquareMovement(Vec2 input) {
        float length = input.length();
        if (length <= 0.0F) {
            return input;
        } else {
            Vec2 multiplied = input.scale(1.0F / length);
            float distance = distanceToUnitSquare(multiplied);
            float min = Math.min(length * distance, 1.0F);
            return multiplied.scale(min);
        }
    }

    private static float distanceToUnitSquare(Vec2 input) {
        float x = Math.abs(input.x());
        float z = Math.abs(input.y());
        float additional = z > x ? x / z : z / x;
        return MathHelper.sqrt_float(1.0F + (additional * additional));
    }


    public IterationResult runTeleportIteration(final Vector vector) {
        final Motion motion = this.motion.clone();
        final List<String> tags = new ArrayList<>();

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

        iteration.getTags().add("teleport");

        return new IterationResult(iteration.getOffset(), iteration, iteration.getPredicted(), iteration.getMotion(),
                iteration.getTags());
    }

    private void applyResetConstant(Motion motion) {
        if(playerVersion >= 767) { //Not a thing in later versions
            return;
        }
        final double RESET = playerVersion > 47 ? MojangConstants.RESET : MojangConstants.RESET_LEGACY;

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
