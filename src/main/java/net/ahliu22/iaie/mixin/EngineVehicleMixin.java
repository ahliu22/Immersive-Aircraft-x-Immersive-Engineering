package net.ahliu22.iaie.mixin;

import net.ahliu22.iaie.Fuel;
import immersive_aircraft.config.Config;
import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(EngineVehicle.class)
public class EngineVehicleMixin implements Fuel {

    @Final
    @Shadow(remap = false)
    private static EntityDataAccessor<Float> UTILIZATION;

    @Final
    @Shadow(remap = false)
    private static EntityDataAccessor<Boolean> LOW_ON_FUEL;

    /**
     * 修改燃料补充逻辑，在补充时消耗1mb流体
     */
    @Inject(method = "refuel*", at = @At("HEAD"), cancellable = true, remap = false)
    private void refuelWithFluid(CallbackInfo ci) {
        EngineVehicle vehicle = (EngineVehicle) (Object) this;

        if (vehicle.level().isClientSide) {
            return;
        }

        List<SlotDescription> fuelSlots = vehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);

        for (int i = 0; i < fuelSlots.size(); i++) {
            SlotDescription slot = fuelSlots.get(i);
            ItemStack stack = vehicle.getInventory().getItem(slot.index());

            if (stack.isEmpty()) continue;

            if (Fuel.isIEBarrel(stack)) {
                refuelFromBarrel(stack, i);
            }
        }
        ci.cancel();
    }

    /**
     * 从金属桶中消耗流体并补充燃料
     */
    @Unique
    private void refuelFromBarrel(ItemStack barrelStack, int fuelIndex) {
        FluidStack fluidStack = Fuel.getFluidFromIEBarrel(barrelStack);

        if (fluidStack.isEmpty()) {
            return;
        }

        if (!Fuel.isBiodiesel(fluidStack.getFluid())) {
            return;
        }

        int currentFuel = getFuelValue(fuelIndex);

        if (currentFuel < 1000) {
            if (fluidStack.getAmount() == 1) fluidStack = null;
            else fluidStack.shrink(1);
            Fuel.updateIEBarrelFluid(barrelStack, fluidStack);

            setFuelValue(fuelIndex, currentFuel + Fuel.BIODIESEL_BURN_RATE);
        }
    }

    @Unique
    private int getFuelValue(int index) {
        try {
            Field fuelField = EngineVehicle.class.getDeclaredField("fuel");
            fuelField.setAccessible(true);
            int[] fuel = (int[]) fuelField.get(this);

            if (index >= 0 && index < fuel.length) {
                return fuel[index];
            }
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
        }
    }

    /**
     * 修改燃料利用率计算，基于生物柴油系统
     */
    @Inject(method = "getFuelUtilization", at = @At("HEAD"), cancellable = true, remap = false)
    private void calculateBiodieselUtilization(CallbackInfoReturnable<Float> cir) {
        EngineVehicle vehicle = (EngineVehicle) (Object) this;

        if (Config.getInstance().fuelConsumption == 0) {
            cir.setReturnValue(1.0f);
            return;
        }
        if (!Config.getInstance().burnFuelInCreative && vehicle.isPilotCreative()) {
            cir.setReturnValue(1.0f);
            return;
        }

        List<SlotDescription> fuelSlots = vehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);
        if (fuelSlots.isEmpty()) {
            cir.setReturnValue(1.0f);
            return;
        }

        int barrelsWithFuel = 0;
        int totalBarrels = 0;

        for (SlotDescription slot : fuelSlots) {
            ItemStack stack = vehicle.getInventory().getItem(slot.index());
            if (Fuel.isIEBarrel(stack)) {
                totalBarrels++;
                if (hasBiodieselInBarrel(stack)) {
                    barrelsWithFuel++;
                }
            }
        }

        if (totalBarrels == 0) {
            cir.setReturnValue(0.0f);
            return;
        }

        float utilization = (float) barrelsWithFuel / totalBarrels;
        boolean lowFuel = isAnyBarrelLowOnBiodiesel(vehicle);

        utilization *= (lowFuel ? 0.75f : 1.0f);

        if (!vehicle.level().isClientSide) {
            vehicle.getEntityData().set(UTILIZATION, utilization);
            vehicle.getEntityData().set(LOW_ON_FUEL, lowFuel);
        }

        cir.setReturnValue(utilization);
    }
}
