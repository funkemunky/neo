package me.hydro.emulator;

import me.hydro.emulator.collision.Block;
import me.hydro.emulator.collision.impl.BlockIce;
import me.hydro.emulator.object.input.DataSupplier;
import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.object.result.IterationResult;
import me.hydro.emulator.util.Vector;
import me.hydro.emulator.util.mcp.AxisAlignedBB;
import me.hydro.emulator.util.mcp.BlockPos;
import me.hydro.emulator.util.mcp.MathHelper;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EmulatorModernVanillaFlowTest {

    private static final DataSupplier EMPTY_WORLD = new DataSupplier() {
        @Override
        public List<AxisAlignedBB> getCollidingBoxes(AxisAlignedBB bb) {
            return Collections.emptyList();
        }

        @Override
        public Block getBlockAt(BlockPos blockPos) {
            return null;
        }
    };

    @Test
    public void modernGroundTickMatchesVanillaPacketPositionExactly() {
        float inputForward = 1.0F * 0.98F;
        float blockFriction = 0.6F;
        float moveSpeed = 0.1F * (0.21600002F
                / (blockFriction * blockFriction * blockFriction));
        double motionZ = (double) inputForward * (double) moveSpeed;
        double packetZ = ((-0.3D + motionZ) + (0.3D + motionZ)) / 2.0D;

        Vector to = new Vector(0.0D, 64.0D, packetZ);
        Emulator emulator = new Emulator(EMPTY_WORLD, 768);
        IterationInput input = IterationInput.builder()
                .forward(1)
                .strafing(0)
                .ground(true)
                .modernMovement(true)
                .aiMoveSpeed(0.1D)
                .fastMathType(MathHelper.FastMathType.MODERN_VANILLA)
                .to(to)
                .lastReportedBoundingBox(new AxisAlignedBB(-0.3D, 64.0D, -0.3D,
                        0.3D, 65.8D, 0.3D))
                .build();

        IterationResult result = emulator.runIteration(input);

        assertEquals(to.getZ(), result.getPredicted().getZ(), 0.0D);
        assertEquals(0.0D, result.getOffset(), 0.0D);
    }

    @Test
    public void modernSlowedDiagonalTickMatchesReferenceInputExactly() {
        float input = 1.0F;
        input *= 0.2D;
        input *= 0.98F;
        float blockFriction = 0.6F;
        float moveSpeed = 0.1F * (0.21600002F
                / (blockFriction * blockFriction * blockFriction));
        double motion = (double) input * (double) moveSpeed;
        double packetPosition = ((-0.3D + motion) + (0.3D + motion)) / 2.0D;

        Vector to = new Vector(packetPosition, 64.0D, packetPosition);
        Emulator emulator = new Emulator(EMPTY_WORLD, 768);
        IterationInput iteration = IterationInput.builder()
                .forward(1)
                .strafing(1)
                .usingItem(true)
                .ground(true)
                .modernMovement(true)
                .aiMoveSpeed(0.1D)
                .fastMathType(MathHelper.FastMathType.MODERN_VANILLA)
                .to(to)
                .lastReportedBoundingBox(new AxisAlignedBB(-0.3D, 64.0D, -0.3D,
                        0.3D, 65.8D, 0.3D))
                .build();

        IterationResult result = emulator.runIteration(iteration);

        assertEquals(to.getX(), result.getPredicted().getX(), 0.0D);
        assertEquals(to.getZ(), result.getPredicted().getZ(), 0.0D);
        assertEquals(0.0D, result.getOffset(), 0.0D);
    }

    @Test
    public void modernTickResetsSmallVerticalMotionBeforeTravel() {
        Vector position = new Vector(0.0D, 64.0D, 0.0D);
        Emulator emulator = new Emulator(EMPTY_WORLD, 768);
        emulator.setMotion(new Motion(0.0D, 0.002D, 0.0D, 0.0F, 0.0F));
        IterationInput input = IterationInput.builder()
                .ground(false)
                .modernMovement(true)
                .fastMathType(MathHelper.FastMathType.MODERN_VANILLA)
                .to(position)
                .lastReportedBoundingBox(new AxisAlignedBB(-0.3D, 64.0D, -0.3D,
                        0.3D, 65.8D, 0.3D))
                .build();

        IterationResult result = emulator.runIteration(input);

        assertEquals(position.getY(), result.getPredicted().getY(), 0.0D);
        assertEquals(0.0D, result.getOffset(), 0.0D);
    }

    @Test
    public void modernTickUsesIndependentReferenceHorizontalResetThresholds() {
        double packetZ = ((-0.3D + 0.002D) + (0.3D + 0.002D)) / 2.0D;
        Vector position = new Vector(0.0D, 64.0D, packetZ);
        Emulator emulator = new Emulator(EMPTY_WORLD, 768);
        emulator.setMotion(new Motion(0.001999999D, 0.0D, 0.002D, 0.0F, 0.0F));
        IterationInput input = IterationInput.builder()
                .ground(false)
                .modernMovement(true)
                .fastMathType(MathHelper.FastMathType.VANILLA)
                .to(position)
                .lastReportedBoundingBox(new AxisAlignedBB(-0.3D, 64.0D, -0.3D,
                        0.3D, 65.8D, 0.3D))
                .build();

        IterationResult result = emulator.runIteration(input);

        assertEquals(position.getX(), result.getPredicted().getX(), 0.0D);
        assertEquals(position.getZ(), result.getPredicted().getZ(), 0.0D);
        assertEquals(0.0D, result.getOffset(), 0.0D);
    }

    @Test
    public void modernGroundTickUsesFrictionAtPreviousPosition() {
        DataSupplier iceAtSource = new DataSupplier() {
            @Override
            public List<AxisAlignedBB> getCollidingBoxes(AxisAlignedBB bb) {
                return Collections.emptyList();
            }

            @Override
            public Block getBlockAt(BlockPos blockPos) {
                return blockPos.getX() == 0 && blockPos.getY() == 63 && blockPos.getZ() == 0
                        ? new BlockIce() : null;
            }
        };
        float inputStrafe = 1.0F * 0.98F;
        float blockFriction = 0.98F;
        float moveSpeed = 0.1F * (0.21600002F
                / (blockFriction * blockFriction * blockFriction));
        double sourceX = 0.99D;
        double motionX = (double) inputStrafe * (double) moveSpeed;
        double packetX = ((sourceX - 0.3D + motionX) + (sourceX + 0.3D + motionX)) / 2.0D;
        Vector to = new Vector(packetX, 64.0D, 0.0D);
        Emulator emulator = new Emulator(iceAtSource, 768);
        IterationInput input = IterationInput.builder()
                .forward(0)
                .strafing(1)
                .ground(true)
                .modernMovement(true)
                .aiMoveSpeed(0.1D)
                .fastMathType(MathHelper.FastMathType.VANILLA)
                .to(to)
                .lastReportedBoundingBox(new AxisAlignedBB(sourceX - 0.3D, 64.0D, -0.3D,
                        sourceX + 0.3D, 65.8D, 0.3D))
                .build();

        IterationResult result = emulator.runIteration(input);

        assertEquals(to.getX(), result.getPredicted().getX(), 0.0D);
        assertEquals(0.0D, result.getOffset(), 0.0D);
    }

    @Test
    public void confirmedTeleportUpdatesBoundingBoxForNextTick() {
        Vector destination = new Vector(102.5D, 70.0D, -35.25D);
        Emulator emulator = new Emulator(EMPTY_WORLD, 768);
        emulator.setLastReportedBoundingBox(new AxisAlignedBB(-0.3D, 64.0D, -0.3D,
                0.3D, 65.8D, 0.3D));

        IterationResult teleport = emulator.runTeleportIteration(destination);
        assertEquals(0.0D, emulator.getLastReportedBoundingBox().resetPositionToBB().getX(), 0.0D);
        emulator.confirm(teleport.getIteration());
        AxisAlignedBB teleportedBox = emulator.getLastReportedBoundingBox();

        IterationInput nextTick = IterationInput.builder()
                .ground(false)
                .modernMovement(true)
                .fastMathType(MathHelper.FastMathType.MODERN_VANILLA)
                .to(destination)
                .lastReportedBoundingBox(new AxisAlignedBB(destination, 0.6D, 1.8D))
                .build();
        IterationResult result = emulator.runIteration(nextTick);

        assertEquals(destination.getX(), result.getPredicted().getX(), 0.0D);
        assertEquals(destination.getY(), result.getPredicted().getY(), 0.0D);
        assertEquals(destination.getZ(), result.getPredicted().getZ(), 0.0D);
        assertEquals(0.0D, result.getOffset(), 0.0D);
        assertEquals(0.6D, teleportedBox.maxX - teleportedBox.minX, 1.0E-12D);
        assertEquals(1.8D, teleportedBox.maxY - teleportedBox.minY, 1.0E-12D);
        assertEquals(0.6D, teleportedBox.maxZ - teleportedBox.minZ, 1.0E-12D);
    }
}
