package me.hydro.emulator.handler.impl.modern;

import me.hydro.emulator.handler.MovementHandler;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.util.mcp.MathHelper;
import me.hydro.emulator.util.mcp.Vec3;

public class ApplyMovementInputHandler implements MovementHandler {
    @Override
    public IterationHolder handle(IterationHolder holder) {
        return null;
    }

    private static Vec3 movementInputToVelocity(Vec3 movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if(d < 1.0E-7) {
            return Vec3.ZERO;
        } else {
            Vec3 vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
            float f = MathHelper.sin(yaw * (float) (Math.PI / 180.0));
            float g = MathHelper.cos(yaw * (float) (Math.PI / 180.0));
            return new Vec3(vec3d.xCoord * g - vec3d.zCoord * f, vec3d.yCoord, vec3d.zCoord * g + vec3d.xCoord * f);
        }
    }
}
