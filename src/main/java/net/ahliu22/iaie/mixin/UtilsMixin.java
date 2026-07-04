package net.ahliu22.iaie.mixin;

import net.ahliu22.iaie.Fuel;
import net.ahliu22.iaie.IaieConfig;
import immersive_aircraft.util.Utils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Utils.class)
public class UtilsMixin {

    @Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getLampFuelTime(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
        if (Fuel.BYPASS_FUEL_TIME.get()) return;
        if (fuel.isEmpty()) return;
        if (Fuel.isIEBarrel(fuel)) {
            cir.setReturnValue(1);
            cir.cancel();
        } else if (IaieConfig.DISABLE_SOLID_FUEL.get()) {
            cir.cancel();
        }
    }
}
