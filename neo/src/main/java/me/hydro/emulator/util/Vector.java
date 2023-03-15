package me.hydro.emulator.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hydro.emulator.collision.Block;
import me.hydro.emulator.util.mcp.BlockPos;
import me.hydro.emulator.util.mcp.MathHelper;

@Data
@AllArgsConstructor
public class Vector {

    private double x, y, z;

    public double distance(final Vector other) {
        final double deltaX = other.getX() - x;
        final double deltaY = other.getY() - y;
        final double deltaZ = other.getZ() - z;

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    public double distanceSqrt(final Vector other) {
        final double deltaX = other.getX() - x;
        final double deltaY = other.getY() - y;
        final double deltaZ = other.getZ() - z;

        return (deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(
                MathHelper.floor_double(x),
                MathHelper.floor_double(y),
                MathHelper.floor_double(z));
    }
}
