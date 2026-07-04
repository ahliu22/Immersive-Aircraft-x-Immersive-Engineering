package net.ahliu22.iaie;

import net.minecraftforge.common.ForgeConfigSpec;

public class IaieConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DISABLE_SOLID_FUEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Immersive Aircraft x Immersive Engineering Configuration");
        builder.push("general");
        DISABLE_SOLID_FUEL = builder
                .comment("If true, solid fuels (such as coal and logs) are disabled in aircraft. Only IE barrels with biodiesel can be used as fuel.")
                .define("disableSolidFuel", true);
        builder.pop();

        SPEC = builder.build();
    }
}
