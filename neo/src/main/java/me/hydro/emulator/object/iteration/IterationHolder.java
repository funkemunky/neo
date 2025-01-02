package me.hydro.emulator.object.iteration;

import lombok.Data;
import me.hydro.emulator.Emulator;
import me.hydro.emulator.object.TagData;
import me.hydro.emulator.object.input.DataSupplier;
import me.hydro.emulator.object.input.IterationInput;
import me.hydro.emulator.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Contains the data for a single iteration of the emulator
 */
@Data

public class IterationHolder {

    private Motion motion;

    private final Emulator emulator;

    private final IterationInput input;
    private final DataSupplier dataSupplier;

    private List<Consumer<Emulator>> postEmulation = new ArrayList<>();

    private List<TagData> tags = new ArrayList<>();

    private float friction;

    private double offset;
    private Vector predicted;

    public void addPostAction(Consumer<Emulator> consumer) {
        postEmulation.add(consumer);
    }
}
