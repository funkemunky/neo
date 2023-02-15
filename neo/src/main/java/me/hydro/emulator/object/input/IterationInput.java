package me.hydro.emulator.object.input;

import lombok.Builder;
import lombok.Data;
import me.hydro.emulator.util.PotionEffect;
import me.hydro.emulator.util.Vector;
import me.hydro.emulator.util.mcp.AxisAlignedBB;

@Data
@Builder
public class IterationInput implements Cloneable {

    private final boolean ground, jumping, sprinting, usingItem, hitSlowdown, sneaking;
    private final int forward, strafing;
    private final float yaw;

    private final AxisAlignedBB lastReportedBoundingBox;
    private final Vector to;
    private final PotionEffect speed, slowness, jumpboost;

    @Override
    public IterationInput clone() {
        return IterationInput.builder()
                .ground(ground)
                .jumping(jumping)
                .sprinting(sprinting)
                .usingItem(usingItem)
                .hitSlowdown(hitSlowdown)
                .sneaking(sneaking)
                .forward(forward)
                .strafing(strafing)
                .yaw(yaw)
                .lastReportedBoundingBox(lastReportedBoundingBox)
                .to(to)
                .speed(speed)
                .slowness(slowness)
                .jumpboost(jumpboost)
                .build();
    }
}
