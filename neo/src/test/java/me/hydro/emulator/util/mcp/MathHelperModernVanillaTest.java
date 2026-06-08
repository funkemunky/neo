package me.hydro.emulator.util.mcp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathHelperModernVanillaTest {

    private static final float SIN_SCALE = 10430.378F;
    private static final int SIN_MASK = 65535;

    @Test
    public void modernVanillaAliasMatchesReferenceLookup() {
        for (float yaw : new float[]{-177.572F, -90.0F, -0.001F, 0.0F, 45.0F, 179.999F}) {
            float radians = yaw * (float) Math.PI / 180.0F;

            assertEquals(referenceSin(radians), MathHelper.sin(MathHelper.FastMathType.MODERN_VANILLA, radians), 0.0F);
            assertEquals(referenceCos(radians), MathHelper.cos(MathHelper.FastMathType.MODERN_VANILLA, radians), 0.0F);
        }
    }

    @Test
    public void modernVanillaAliasMatchesVanillaAtBoundaryAngle() {
        float radians = -177.572F * (float) Math.PI / 180.0F;

        assertEquals(MathHelper.sin(MathHelper.FastMathType.MODERN_VANILLA, radians),
                MathHelper.sin(MathHelper.FastMathType.VANILLA, radians), 0.0F);
    }

    private static float referenceSin(float radians) {
        int index = (int) (radians * SIN_SCALE) & SIN_MASK;
        return tableValue(index);
    }

    private static float referenceCos(float radians) {
        int index = (int) (radians * SIN_SCALE + 16384.0F) & SIN_MASK;
        return tableValue(index);
    }

    private static float tableValue(int index) {
        return (float) Math.sin((double) index * Math.PI * 2.0D / 65536.0D);
    }
}
