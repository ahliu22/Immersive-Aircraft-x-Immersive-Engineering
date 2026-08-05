package net.ahliu22.iaie.mixin;

import com.mojang.logging.LogUtils;
import net.ahliu22.iaie.Fuel;
import net.ahliu22.iaie.IaieConfig;
import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(EngineVehicle.class)
public class EngineVehicleMixin implements Fuel {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Unique
    private int getFuelValue(int index) {
        try {
            Field fuelField = EngineVehicle.class.getDeclaredField("fuel");
            fuelField.setAccessible(true);
            int[] fuel = (int[]) fuelField.get(this);
            if (index >= 0 && index < fuel.length) {
                return fuel[index];
            }
        } catch (Exception e) {
            LOGGER.error("[IAIE] Failed to get fuel value: {}", e.getMessage());
        }
        return 0;
    }

    @Unique
    private void setFuelValue(int index, int value) {
        try {
            Field fuelField = EngineVehicle.class.getDeclaredField("fuel");
            fuelField.setAccessible(true);
            int[] fuel = (int[]) fuelField.get(this);
            if (index >= 0 && index < fuel.length) {
                fuel[index] = value;
            }
        } catch (Exception e) {
            LOGGER.error("[IAIE] Failed to set fuel value: {}", e.getMessage());
        }
    }

    @Inject(method = "refuel(I)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void handleBarrelRefuel(int i, CallbackInfo ci) {
        EngineVehicle vehicle = (EngineVehicle) (Object) this;

        if (vehicle.level().isClientSide) {
            ci.cancel();
            return;
        }

        List<SlotDescription> slots = vehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);
        if (i >= slots.size()) {
            ci.cancel();
            return;
        }

        ItemStack stack = vehicle.getInventory().getItem(slots.get(i).index());

        if (Fuel.isIEBarrel(stack)) {
            int currentFuel = getFuelValue(i);
            LOGGER.debug("[IAIE] Found IE barrel in slot {}, fuel={}, TARGET={}", i, currentFuel, EngineVehicle.TARGET_FUEL);

            if (currentFuel < EngineVehicle.TARGET_FUEL) {
                FluidStack fluid = Fuel.getFluidFromIEBarrel(stack);
                LOGGER.debug("[IAIE] Barrel fluid: {}", fluid.isEmpty() ? "EMPTY" : fluid.getAmount() + "mb of " + fluid.getFluid());

                if (!fluid.isEmpty() && Fuel.isBiodiesel(fluid.getFluid())) {
                    if (Fuel.drainFromIEBarrel(stack, 1)) {
                        setFuelValue(i, currentFuel + IaieConfig.BIODIESEL_BURN_RATE.get());
                        LOGGER.debug("[IAIE] Drained 1mb, fuel now {}", currentFuel + IaieConfig.BIODIESEL_BURN_RATE.get());
                    } else {
                        LOGGER.debug("[IAIE] Drain failed!");
                    }
                } else {
                    LOGGER.debug("[IAIE] Fluid not biodiesel or empty");
                }
            }
            ci.cancel();
            return;
        }

        if (IaieConfig.DISABLE_SOLID_FUEL.get()) {
            ci.cancel();
        }
    }
}
