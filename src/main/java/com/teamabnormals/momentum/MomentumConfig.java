package com.teamabnormals.momentum;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class MomentumConfig {

    public static class Common {
        public final ConfigValue<Boolean> noMoving;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Momentum common configuration").push("common");
            builder.push("mode");

            noMoving = builder.comment("Whether the momentum caps if a player moves. If false, it will cap after a certain amount of blocks, like originally. Default: true")
                    .define("No Moving", true);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }

}
