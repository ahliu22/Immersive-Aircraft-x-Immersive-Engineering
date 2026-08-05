package net.ahliu22.iaie;

import net.neoforged.neoforge.common.ModConfigSpec;

public class IaieConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue DISABLE_SOLID_FUEL;
    public static final ModConfigSpec.IntValue BIODIESEL_BURN_RATE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Immersive Aircraft x Immersive Engineering Configuration");
        builder.push("general");
        DISABLE_SOLID_FUEL = builder
                .comment("If true, solid fuels (such as coal and logs) are disabled in aircraft. Only IE barrels with biodiesel can be used as fuel.")
                .define("disableSolidFuel", true);
        BIODIESEL_BURN_RATE = builder
                .comment("Working ticks per 1 mb of liquid fuel consumed. A greater value means fuel can burn for longer time.")
                .defineInRange("biodieselBurnRate", 20, 1, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }
}
