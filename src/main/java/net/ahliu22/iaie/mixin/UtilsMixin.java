package net.ahliu22.iaie.mixin;

import net.ahliu22.iaie.Fuel;
import immersive_aircraft.util.Utils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Utils.class)
public class UtilsMixin {


    /**
     * 修改燃料时间获取，从ie金属桶获取燃料值
     */
    @Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getLampFuelTime(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
        if (fuel.isEmpty()) return;
        if (Fuel.isIEBarrel(fuel)) {
            cir.setReturnValue(1);
        }
        cir.cancel();
    }
}