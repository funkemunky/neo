package me.hydro.emulator;

import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.util.Vec2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmulatorInputCalculationTest {

    @Test
    public void modernCardinalInputKeepsVanillaScale() {
        List<String> tags = new ArrayList<>();

        Vec2 result = modernInput(1, 0, false, false, tags);

        assertVec2(0.98F, 0.0F, result);
        assertTrue(tags.isEmpty());
    }

    @Test
    public void modernDiagonalInputPreservesReferenceComponents() {
        Vec2 result = modernInput(1, 1, false, false, new ArrayList<>());

        assertVec2(0.98F, 0.98F, result);
    }

    @Test
    public void modernDiagonalItemUseMatchesReferenceOrder() {
        List<String> tags = new ArrayList<>();

        Vec2 result = modernInput(1, 1, true, false, tags);

        assertVec2(0.19600001F, 0.19600001F, result);
        assertEquals(List.of("using"), tags);
    }

    @Test
    public void modernDiagonalSneakingMatchesReferenceOrder() {
        List<String> tags = new ArrayList<>();

        Vec2 result = modernInput(1, 1, false, true, tags);

        assertVec2(0.29400003F, 0.29400003F, result);
        assertEquals(List.of("sneaking"), tags);
    }

    @Test
    public void modernDiagonalCombinedSlowdownsPreserveVanillaOrder() {
        List<String> tags = new ArrayList<>();

        Vec2 result = modernInput(1, 1, true, true, 0.3F, tags);

        assertVec2(0.058800004F, 0.058800004F, result);
        assertEquals(List.of("using", "sneaking"), tags);
    }

    @Test
    public void modernSneakingUsesReferenceSlowdown() {
        List<String> tags = new ArrayList<>();

        Vec2 result = modernInput(1, 1, true, true, 0.6F, tags);

        assertVec2(0.058800004F, 0.058800004F, result);
        assertEquals(List.of("using", "sneaking"), tags);
    }

    @Test
    public void legacyDiagonalItemUseCalculationRemainsUnchanged() {
        List<String> tags = new ArrayList<>();
        IterationInput input = input(1, 1, true, false);

        Vec2 result = Emulator.getResultingInput(input, tags);

        assertVec2(0.19600001F, 0.19600001F, result);
        assertEquals(List.of("using"), tags);
    }

    private static Vec2 modernInput(int forward, int strafing, boolean usingItem, boolean sneaking,
                                    List<String> tags) {
        return modernInput(forward, strafing, usingItem, sneaking, 0.3F, tags);
    }

    private static Vec2 modernInput(int forward, int strafing, boolean usingItem, boolean sneaking,
                                    float sneakingSpeed, List<String> tags) {
        return Emulator.getModernResultingInput(input(forward, strafing, usingItem, sneaking, sneakingSpeed), tags);
    }

    private static IterationInput input(int forward, int strafing, boolean usingItem, boolean sneaking) {
        return input(forward, strafing, usingItem, sneaking, 0.3F);
    }

    private static IterationInput input(int forward, int strafing, boolean usingItem, boolean sneaking,
                                        float sneakingSpeed) {
        return IterationInput.builder()
                .forward(forward)
                .strafing(strafing)
                .usingItem(usingItem)
                .sneaking(sneaking)
                .sneakingSpeed(sneakingSpeed)
                .build();
    }

    private static void assertVec2(float expectedX, float expectedY, Vec2 actual) {
        assertEquals(expectedX, actual.x(), 0.0F);
        assertEquals(expectedY, actual.y(), 0.0F);
    }
}
