package me.hydro.emulator.util;

/**
 * Here we house the various "magic values" that
 * Mojang uses.
 */
public interface MojangConstants {

    float SPEED_AIR = 0.02F;
    float SPEED_AIR_SPRINTING = 0.025999999F;
    float LAND_MOVEMENT_FACTOR_LEGACY = 0.16277136F;

    float UPWARDS_MOTION = 0.42F;

    double SPRINT_MULTIPLIER = 0.30000001192092896D; //Don't be a dick Hydrogen, I gave the real value :)
    double GRAVITY = 0.9800000190734863D;
    double RESET_LEGACY = 0.005D;

    double RESET = 0.003D;
}
