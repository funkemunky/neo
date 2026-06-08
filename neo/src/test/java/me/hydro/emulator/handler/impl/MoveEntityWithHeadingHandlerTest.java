package me.hydro.emulator.handler.impl;

import me.hydro.emulator.util.PotionEffect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveEntityWithHeadingHandlerTest {

    @Test
    public void slownessAppliesNegativeTotalModifierWithoutReversingMovement() {
        PotionEffect slowness = PotionEffect.builder()
                .amplifier(0)
                .build();

        double result = MoveEntityWithHeadingHandler.getAiMoveSpeed(null, slowness, 0.1D, false);

        assertEquals(0.08499999940395356D, result, 0.0D);
    }

    @Test
    public void speedSlownessAndSprintModifiersCompose() {
        PotionEffect speed = PotionEffect.builder()
                .amplifier(0)
                .build();
        PotionEffect slowness = PotionEffect.builder()
                .amplifier(0)
                .build();

        double result = MoveEntityWithHeadingHandler.getAiMoveSpeed(speed, slowness, 0.1D, true);

        double expected = 0.1D;
        expected += 0.20000000298023224D * expected;
        expected += -0.15000000596046448D * expected;
        expected += 0.30000001192092896D * expected;

        assertEquals(expected, result, 0.0D);
    }
}
