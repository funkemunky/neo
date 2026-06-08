package me.hydro.emulator.handler.impl;

import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.util.PotionEffect;
import me.hydro.emulator.util.mcp.MathHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JumpHandlerTest {

    private final JumpHandler handler = new JumpHandler();

    @Test
    public void modernSprintJumpMatchesReferenceFloatScaling() {
        float yaw = -177.572F;
        float radians = yaw * ((float) Math.PI / 180.0F);
        IterationHolder iteration = iteration(yaw, null, 0.0D);

        handler.handle(iteration);

        assertEquals((double) (-referenceSin(radians) * 0.2F),
                iteration.getMotion().getMotionX(), 0.0D);
        assertEquals((double) (referenceCos(radians) * 0.2F),
                iteration.getMotion().getMotionZ(), 0.0D);
    }

    @Test
    public void modernJumpBoostRoundsAsFloatBeforeMotionAssignment() {
        PotionEffect jumpBoost = PotionEffect.builder()
                .amplifier(0)
                .build();
        IterationHolder iteration = iteration(0.0F, jumpBoost, 0.0D);

        handler.handle(iteration);

        assertEquals((double) (0.42F + 0.1F), iteration.getMotion().getMotionY(), 0.0D);
    }

    @Test
    public void modernJumpDoesNotLowerExistingVerticalMotion() {
        IterationHolder iteration = iteration(0.0F, null, 0.5D);

        handler.handle(iteration);

        assertEquals(0.5D, iteration.getMotion().getMotionY(), 0.0D);
    }

    private static IterationHolder iteration(float yaw, PotionEffect jumpBoost, double motionY) {
        IterationInput input = IterationInput.builder()
                .modernMovement(true)
                .sprinting(true)
                .effectJump(jumpBoost)
                .fastMathType(MathHelper.FastMathType.VANILLA)
                .yaw(yaw)
                .build();
        IterationHolder iteration = new IterationHolder(null, input, null);

        iteration.setMotion(new Motion(0.0D, motionY, 0.0D, 0.0F, 0.0F));
        return iteration;
    }

    private static float referenceSin(float radians) {
        int index = (int) (radians * 10430.378F) & 65535;
        return (float) Math.sin((double) index * Math.PI * 2.0D / 65536.0D);
    }

    private static float referenceCos(float radians) {
        int index = (int) (radians * 10430.378F + 16384.0F) & 65535;
        return (float) Math.sin((double) index * Math.PI * 2.0D / 65536.0D);
    }
}
