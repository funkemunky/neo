package me.hydro.emulator.object.result;

import me.hydro.emulator.object.MoveTag;
import me.hydro.emulator.object.TagData;
import me.hydro.emulator.object.iteration.IterationHolder;
import me.hydro.emulator.object.iteration.Motion;
import me.hydro.emulator.util.Vector;

import java.util.Set;


public record IterationResult(double offset, IterationHolder iteration, Vector predicted, Motion motion,
                              Set<TagData> tags) {

    public boolean containsTag(MoveTag tag) {
        return tags.contains(tag.getTagData());
    }
}
