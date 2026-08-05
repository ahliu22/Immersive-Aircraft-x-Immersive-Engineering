package net.ahliu22.iaie;

import blusunrize.immersiveengineering.common.register.IEDataComponents;
import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.List;

public interface Fuel {

    static boolean isIEBarrel(ItemStack stack) {
        return stack.is(ItemTags.create(ResourceLocation.parse("iaie:fuel_tanks")));
    }

    static boolean isBiodiesel(Fluid fluid) {
        if (fluid == null) return false;
        if (fluid.is(FluidTags.create(ResourceLocation.parse("iaie:fuel")))) {
            return true;
        }
        return false;
    }

    static FluidStack getFluidFromIEBarrel(ItemStack barrelStack) {
        if (barrelStack.isEmpty()) return FluidStack.EMPTY;
        SimpleFluidContent content = barrelStack.get(IEDataComponents.GENERIC_FLUID);
        if (content == null) return FluidStack.EMPTY;
        return content.copy();
    }

    static boolean drainFromIEBarrel(ItemStack barrelStack, int amount) {
        SimpleFluidContent content = barrelStack.get(IEDataComponents.GENERIC_FLUID);
        if (content == null) return false;
        FluidStack fluid = content.copy();
        if (fluid.isEmpty() || fluid.getAmount() < amount) return false;

        int newAmount = fluid.getAmount() - amount;
        if (newAmount <= 0) {
            barrelStack.remove(IEDataComponents.GENERIC_FLUID);
        } else {
            barrelStack.set(IEDataComponents.GENERIC_FLUID,
                    SimpleFluidContent.copyOf(fluid.copyWithAmount(newAmount)));
        }
        return true;
    }

    default boolean hasBiodieselInBarrel(ItemStack barrelStack) {
        FluidStack fluid = getFluidFromIEBarrel(barrelStack);
        if (fluid.isEmpty()) return false;
        return isBiodiesel(fluid.getFluid()) && fluid.getAmount() > 0;
    }

    default boolean isAnyBarrelLowOnBiodiesel(EngineVehicle vehicle) {
        List<SlotDescription> fuelSlots = vehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);
        for (SlotDescription slot : fuelSlots) {
            ItemStack stack = vehicle.getInventory().getItem(slot.index());
            if (isIEBarrel(stack)) {
                FluidStack fluid = getFluidFromIEBarrel(stack);
                if (fluid.isEmpty() || !isBiodiesel(fluid.getFluid()) || fluid.getAmount() <= 100) {
                    return true;
                }
            }
        }
        return false;
    }

}
