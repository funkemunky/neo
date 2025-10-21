package me.hydro.emulator.object.input;

import lombok.Builder;
import lombok.Data;
import me.hydro.emulator.Emulator;
import me.hydro.emulator.util.PotionEffect;
import me.hydro.emulator.util.Vector;
import me.hydro.emulator.util.mcp.AxisAlignedBB;
import me.hydro.emulator.util.mcp.MathHelper;

@Data
@Builder
public class IterationInput implements Cloneable {

    private final boolean ground, jumping, sprinting, usingItem, hitSlowdown, sneaking, modernMovement;
    private boolean waitingForTeleport;
    private final int forward, strafing;
    private final float upwardSpeed;
    private final float yaw;
    private final double aiMoveSpeed;
    private final MathHelper.FastMathType fastMathType;

    private final AxisAlignedBB lastReportedBoundingBox;
    private final Vector to;
    private final PotionEffect effectSpeed, effectSlow, effectJump;

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
                .modernMovement(modernMovement)
                .aiMoveSpeed(aiMoveSpeed)
                .fastMathType(fastMathType)
                .lastReportedBoundingBox(lastReportedBoundingBox)
                .to(to)
                .effectSpeed(effectSpeed)
                .effectSlow(effectSlow)
                .effectJump(effectJump)
                .waitingForTeleport(waitingForTeleport)
                .build();
    }

    public AxisAlignedBB getLastReportedBoundingBox(Emulator emulator) {
        return emulator.getLastReportedBoundingBox() == null
                ? lastReportedBoundingBox : emulator.getLastReportedBoundingBox();
    }


}
