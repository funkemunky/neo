package me.hydro.emulator.handler.impl;

import me.hydro.emulator.handler.MovementHandler;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.util.mcp.MathHelper;
import me.hydro.emulator.util.mcp.Vec3;

public class ApplyMovementInputHandler implements MovementHandler {
    @Override
    public IterationHolder handle(IterationHolder iteration) {
        updateVelocity(getMovementSpeed(iteration, iteration.getFriction()), iteration);
        return null;
    }

    private void updateVelocity(float moveSpeed, IterationHolder iter) {
        Vec3 vec = movementInputToVelocity(
                new Vec3(iter.getInput().getStrafing(), 0, iter.getInput().getForward()),
                moveSpeed,
                iter.getInput().getYaw()
        );

        iter.getMotion().addX(vec.xCoord);
        iter.getMotion().addY(vec.yCoord);
        iter.getMotion().addZ(vec.zCoord);
    }

    private Vec3 movementInputToVelocity(Vec3 movementInput, float speed, float yaw) {
        double combined = movementInput.lengthSquared();

        if(combined < 1.0E-7) {
            return new Vec3(0, 0, 0);
        }

        Vec3 vec3d = (combined > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * (float) (Math.PI / 180.0));
        float g = MathHelper.cos(yaw * (float) (Math.PI / 180.0));
        return new Vec3(vec3d.xCoord * g - vec3d.zCoord * f, vec3d.yCoord, vec3d.zCoord * g + vec3d.xCoord * f);
    }


    private float getMovementSpeed(IterationHolder iter, float slipperiness) {
        return iter.getInput().isGround()
                ? (float)iter.getInput().getAiMoveSpeed() * (0.21600002F / (slipperiness * slipperiness * slipperiness))
                : (float)iter.getInput().getAiMoveSpeed();
    }
}
