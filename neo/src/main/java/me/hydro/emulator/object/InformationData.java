package me.hydro.emulator.object;

import lombok.Getter;

@Getter
public class InformationData extends TagData {

    private final String info;

    public InformationData(MoveTag moveTag, String info) {
        super(moveTag);
        this.info = info;
    }

    public String toString() {
        return getMoveTag().getName() + " (" + info + ")";
    }
}
