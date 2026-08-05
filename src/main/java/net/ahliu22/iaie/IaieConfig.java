package net.ahliu22.iaie;

import net.minecraftforge.common.ForgeConfigSpec;

public class IaieConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DISABLE_SOLID_FUEL;
    public static final ForgeConfigSpec.IntValue BIODIESEL_FUEL_TICK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Immersive Aircraft x Immersive Engineering Configuration");
        builder.push("general");
        DISABLE_SOLID_FUEL = builder
                .comment("If true, solid fuels (such as coal and logs) are disabled in aircraft. Only IE barrels with biodiesel can be used as fuel.")
                .define("disableSolidFuel", true);
        BIODIESEL_FUEL_TICK = builder
                .comment("Working ticks per 1 mb of liquid fuel consumed. A greater value means fuel can burn for longer time.")
                .defineInRange("biodieselFuelTick", 20, 1, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }
}
