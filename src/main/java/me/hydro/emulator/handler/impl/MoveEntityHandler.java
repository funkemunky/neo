package me.hydro.emulator.handler.impl;

import me.hydro.emulator.handler.MovementHandler;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.util.Vector;
import me.hydro.emulator.util.mcp.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public class MoveEntityHandler implements MovementHandler {

    /**
     * The dogshit code inside here can be traced back to the best
     * game development studio in the world, Mojang.
     *
     * Entity#moveEntity
     */

    @Override
    public IterationHolder handle(IterationHolder iteration) {
        double x = iteration.getMotion().getMotionX();
        double y = iteration.getMotion().getMotionY();
        double z = iteration.getMotion().getMotionZ();

        double d3 = x;
        double d4 = y;
        double d5 = z;

        final AxisAlignedBB lastReportedBoundingBox = iteration.getInput().getLastReportedBoundingBox();
        final boolean edges = iteration.getInput().isSneaking() && iteration.getInput().isGround();

        if (edges) {
            iteration.getTags().add("edges");

            double magicSteppingValue = 0.05D;

            for (; x != 0.0D && iteration.getDataSupplier().getCollidingBoxes(lastReportedBoundingBox
                    .offset(x, -1.0D, 0.0D)).isEmpty(); d3 = x) {
                if (x < magicSteppingValue && x >= -magicSteppingValue) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= magicSteppingValue;
                } else x += magicSteppingValue;

                d3 = x;
            }

            for (; z != 0.0D && iteration.getDataSupplier().getCollidingBoxes(lastReportedBoundingBox
                    .offset(0.0D, -1.0D, z)).isEmpty(); d5 = z) {
                if (z < magicSteppingValue && z >= -magicSteppingValue) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= magicSteppingValue;
                } else z += magicSteppingValue;

                d5 = z;
            }

            for (; x != 0.0D && z != 0.0D && iteration.getDataSupplier().getCollidingBoxes(lastReportedBoundingBox
                    .offset(x, -1.0D, z)).isEmpty(); d5 = z) {
                if (x < magicSteppingValue && x >= -magicSteppingValue) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= magicSteppingValue;
                } else x += magicSteppingValue;

                d3 = x;

                if (z < magicSteppingValue && z >= -magicSteppingValue) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= magicSteppingValue;
                } else z += magicSteppingValue;

                d5 = z;
            }
        }

        final AxisAlignedBB bb = lastReportedBoundingBox.clone();
        final List<AxisAlignedBB> collidingBoxes = new ArrayList<>(iteration.getDataSupplier().getCollidingBoxes(bb.addCoord(x, y, z)));

        AxisAlignedBB entityBB = lastReportedBoundingBox;

        for (AxisAlignedBB axisalignedbb1 : collidingBoxes) {
            y = axisalignedbb1.calculateYOffset(entityBB, y);
        }

        entityBB = entityBB.offset(0.0D, y, 0.0D);

        for (AxisAlignedBB axisalignedbb2 : collidingBoxes) {
            x = axisalignedbb2.calculateXOffset(entityBB, x);
        }

        entityBB = entityBB.offset(x, 0.0D, 0.0D);

        for (AxisAlignedBB axisalignedbb13 : collidingBoxes) {
            z = axisalignedbb13.calculateZOffset(entityBB, z);
        }

        entityBB = entityBB.offset(0.0D, 0.0D, z);

        // Step handling
        final float stepHeight = 0.6F;
        final boolean flag1 = iteration.getInput().isGround() || d4 != y && d4 < 0.0D;

        // I am not bothering to clean up this absolute abomination of an if block
        // This is Mojang's dogshit, not mine
        if (flag1 && (d3 != x || d5 != z)) {
            iteration.getTags().add("step");

            double d11 = x;
            double d7 = y;
            double d8 = z;

            AxisAlignedBB axisalignedbb3 = entityBB;
            entityBB = bb.clone();

            y = stepHeight;

            List<AxisAlignedBB> list = new ArrayList<>(iteration.getDataSupplier().getCollidingBoxes(bb.addCoord(d3, y, d5)));

            AxisAlignedBB axisalignedbb4 = entityBB;
            AxisAlignedBB axisalignedbb5 = axisalignedbb4.addCoord(d3, 0.0D, d5);

            double d9 = y;

            for (AxisAlignedBB axisalignedbb6 : list) {
                d9 = axisalignedbb6.calculateYOffset(axisalignedbb5, d9);
            }

            axisalignedbb4 = axisalignedbb4.offset(0.0D, d9, 0.0D);
            double d15 = d3;

            for (AxisAlignedBB axisalignedbb7 : list) {
                d15 = axisalignedbb7.calculateXOffset(axisalignedbb4, d15);
            }

            axisalignedbb4 = axisalignedbb4.offset(d15, 0.0D, 0.0D);
            double d16 = d5;

            for (AxisAlignedBB axisalignedbb8 : list) {
                d16 = axisalignedbb8.calculateZOffset(axisalignedbb4, d16);
            }

            axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d16);

            AxisAlignedBB axisalignedbb14 = entityBB;
            double d17 = y;

            for (AxisAlignedBB axisalignedbb9 : list) {
                d17 = axisalignedbb9.calculateYOffset(axisalignedbb14, d17);
            }

            axisalignedbb14 = axisalignedbb14.offset(0.0D, d17, 0.0D);
            double d18 = d3;

            for (AxisAlignedBB axisalignedbb10 : list) {
                d18 = axisalignedbb10.calculateXOffset(axisalignedbb14, d18);
            }

            axisalignedbb14 = axisalignedbb14.offset(d18, 0.0D, 0.0D);
            double d19 = d5;

            for (AxisAlignedBB axisalignedbb11 : list) {
                d19 = axisalignedbb11.calculateZOffset(axisalignedbb14, d19);
            }

            axisalignedbb14 = axisalignedbb14.offset(0.0D, 0.0D, d19);
            double d20 = d15 * d15 + d16 * d16;
            double d10 = d18 * d18 + d19 * d19;

            if (d20 > d10) {
                x = d15;
                z = d16;
                y = -d9;
                entityBB = axisalignedbb4;
            } else {
                x = d18;
                z = d19;
                y = -d17;
                entityBB = axisalignedbb14;
            }

            for (AxisAlignedBB axisalignedbb12 : list) {
                y = axisalignedbb12.calculateYOffset(entityBB, y);
            }

            entityBB = entityBB.offset(0.0D, y, 0.0D);

            if (d11 * d11 + d8 * d8 >= x * x + z * z) {
                x = d11;
                y = d7;
                z = d8;
                entityBB = axisalignedbb3;
            }
        }

        // We're not handling special case collisions (such as soul sand, slime, etc.). Good luck have fun :)

        final Vector predicted = resetPositionToBB(entityBB);
        final double offset = iteration.getInput().getTo().distance(predicted);

        iteration.setPredicted(predicted);
        iteration.setOffset(offset);

        if (d3 != x) iteration.getMotion().setMotionX(0.0D);
        if (d5 != z) iteration.getMotion().setMotionZ(0.0D);
        if (d4 != y) iteration.getMotion().setMotionY(0.0D);

        return iteration;
    }

    private Vector resetPositionToBB(final AxisAlignedBB bb) {
        double x = (bb.minX + bb.maxX) / 2.0D;
        double z = (bb.minZ + bb.maxZ) / 2.0D;
        double y = bb.minY;

        return new Vector(x, y, z);
    }
}
