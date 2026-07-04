package net.ahliu22.iaie;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Iaie.MODID)
public class Iaie {
    public final static String MODID = "iaie";

    public Iaie() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IaieConfig.SPEC);
    }
}
