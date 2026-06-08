package me.hydro.emulator.handler.impl;

import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.util.mcp.MathHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MoveFlyingHandlerTest {

    private final MoveFlyingHandler handler = new MoveFlyingHandler();

    @Test
    public void modernMovementPreservesDoubleScalePrecision() {
        IterationHolder modern = iteration(true, 0.0F, 0.98F, 0.1F, 0.0F);
        IterationHolder legacy = iteration(false, 0.0F, 0.98F, 0.1F, 0.0F);

        handler.handle(modern);
        handler.handle(legacy);

        double expected = (double) 0.98F * (double) 0.1F;
        assertEquals(expected, modern.getMotion().getMotionZ(), 0.0D);
        assertNotEquals(legacy.getMotion().getMotionZ(), modern.getMotion().getMotionZ(), 0.0D);
    }

    @Test
    public void modernMovementMatchesVanillaVec3NormalizationAndRotation() {
        float strafe = 0.98F;
        float forward = 0.98F;
        float moveSpeed = 0.13F;
        float yaw = -177.572F;
        IterationHolder iteration = iteration(true, strafe, forward, moveSpeed, yaw);

        handler.handle(iteration);

        double length = Math.sqrt((double) strafe * (double) strafe + (double) forward * (double) forward);
        double scaledStrafe = ((double) strafe / length) * (double) moveSpeed;
        double scaledForward = ((double) forward / length) * (double) moveSpeed;
        float radians = yaw * (float) Math.PI / 180.0F;
        float sin = referenceSin(radians);
        float cos = referenceCos(radians);

        assertEquals(scaledStrafe * (double) cos - scaledForward * (double) sin,
                iteration.getMotion().getMotionX(), 0.0D);
        assertEquals(scaledForward * (double) cos + scaledStrafe * (double) sin,
                iteration.getMotion().getMotionZ(), 0.0D);
    }

    @Test
    public void modernMovementUsesVanillaLateralInputSigns() {
        IterationHolder aKey = iteration(true, 0.98F, 0.0F, 0.1F, 0.0F);
        IterationHolder dKey = iteration(true, -0.98F, 0.0F, 0.1F, 0.0F);

        handler.handle(aKey);
        handler.handle(dKey);

        double expected = (double) 0.98F * (double) 0.1F;
        assertEquals(expected, aKey.getMotion().getMotionX(), 0.0D);
        assertEquals(-expected, dKey.getMotion().getMotionX(), 0.0D);
    }

    private static IterationHolder iteration(boolean modernMovement, float strafe, float forward,
                                             float moveSpeed, float yaw) {
        IterationInput input = IterationInput.builder()
                .modernMovement(modernMovement)
                .fastMathType(MathHelper.FastMathType.VANILLA)
                .yaw(yaw)
                .build();
        IterationHolder iteration = new IterationHolder(null, input, null);

        iteration.setMotion(new Motion(0.0D, 0.0D, 0.0D, forward, strafe));
        iteration.setFriction(moveSpeed);
        return iteration;
    }

    private static float referenceSin(float radians) {
        return referenceTableValue(radians);
    }

    private static float referenceCos(float radians) {
        int index = (int) (radians * 10430.378F + 16384.0F) & 65535;
        return tableValue(index);
    }

    private static float referenceTableValue(double radians) {
        int index = (int) (radians * 10430.378F) & 65535;
        return tableValue(index);
    }

    private static float tableValue(int index) {
        return (float) Math.sin((double) index * Math.PI * 2.0D / 65536.0D);
    }
}
