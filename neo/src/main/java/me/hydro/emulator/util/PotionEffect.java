package me.hydro.emulator.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PotionEffect {
    private int amplifier;
    private PotionEffectType type;
}
