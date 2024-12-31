package me.hydro.emulator.object;

import lombok.Getter;

import java.util.Objects;


@Getter
public class TagData {
    private final MoveTag moveTag;

    public TagData(MoveTag moveTag) {
        this.moveTag = moveTag;
    }

    public String toString() {
        return moveTag.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TagData tagData)) return false;
        return moveTag == tagData.moveTag;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moveTag);
    }
}
