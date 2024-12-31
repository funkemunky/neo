package me.hydro.emulator.object;

import lombok.Getter;

@Getter
public enum MoveTag {

    SNEAKING("Sneaking"),
    USING("Using"),
    HIT_SLOW("Hit_Slow"),
    JUMP("Jump"),
    TELEPORT("Teleport"),
    WEB("Web"),
    EDGES("Edges"),
    X_COLLIDED("X_Collided"),
    Y_COLLIDED("Y_Collided"),
    Z_COLLIDED("Z_Collided"),
    STEP("Step"),
    LANDED("Landed"),
    SOUL_SAND("Soul_Sand"),
    SLIME("Slime"),
    SLIME_LANDED("Slime_Landed"),
    GROUND("Ground"),
    BAD_OFFSET("Bad_Offset"),
    VELOCITY("Velocity"),
    SLOP_MOVE("Slop_Move (003)"),
    AIR("Air");

    private final String name;
    private final TagData tagData;

    MoveTag(String name) {
        this.name = name;
        this.tagData = new TagData(this);
    }
}
