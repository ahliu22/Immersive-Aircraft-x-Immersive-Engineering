package net.ahliu22.iaie;

import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public interface Fuel {
    /**
     * 检查是否为IE金属桶
     */
    static boolean isIEBarrel(ItemStack stack) {
        return stack.is(ItemTags.create(new ResourceLocation("immersiveengineering", "metal_barrel")));
    }

    /**
     * 检查流体是否为IE钻头燃料
     */
    static boolean isBiodiesel(Fluid fluid) {
        if (fluid == null) return false;
        if (fluid.is(FluidTags.create(new ResourceLocation("iaie", "fuel")))) {
            return true;
        }
        ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
        if (fluidId != null) {
            String path = fluidId.getPath();
            if (path.contains("diesel") || path.contains("fuel") || path.contains("oil")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从IE金属桶中获取流体
     */
    static FluidStack getFluidFromIEBarrel(ItemStack barrelStack) {
        if (barrelStack.isEmpty()) {
            return FluidStack.EMPTY;
        }

        CompoundTag tag = barrelStack.getTag();
        if (tag == null) {
            return FluidStack.EMPTY;
        }

        if (!tag.contains("tank")) {
            return FluidStack.EMPTY;
        }

        CompoundTag tankTag = tag.getCompound("tank");
        if (tankTag.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int amount = tankTag.getInt("Amount");
        String fluidName = tankTag.getString("FluidName");

        if (amount <= 0 || fluidName == null || fluidName.isEmpty()) {
            return FluidStack.EMPTY;
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
        if (fluid == null) {
            return FluidStack.EMPTY;
        }

        return new FluidStack(fluid, amount);
    }

    /**
     * 检查桶内是否有油
     */
    default boolean hasBiodieselInBarrel(ItemStack barrelStack) {
        FluidStack fluid = getFluidFromIEBarrel(barrelStack);
        if (fluid.isEmpty()) {
            return false;
        }
        return isBiodiesel(fluid.getFluid()) && fluid.getAmount() > 0;
    }

    /**
     * 检查是否有桶油不足
     */
    default boolean isAnyBarrelLowOnBiodiesel(EngineVehicle vehicle) {
        List<SlotDescription> fuelSlots = vehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);

        for (SlotDescription slot : fuelSlots) {
            ItemStack stack = vehicle.getInventory().getItem(slot.index());
            if (isIEBarrel(stack)) {
                if (isBarrelLowOnBiodiesel(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查单个桶是否生物柴油不足
     */
    private boolean isBarrelLowOnBiodiesel(ItemStack barrelStack) {
        FluidStack fluid = Fuel.getFluidFromIEBarrel(barrelStack);
        if (fluid.isEmpty() || !isBiodiesel(fluid.getFluid())) {
            return true;
        }

        // 低燃料阈值：剩余燃料低于100mb（1000tick的消耗量）
        return fluid.getAmount() <= 100;
    }

    /**
     * 燃烧率：0.1mb/tick
     */
    int BIODIESEL_BURN_RATE = 20;

    /**
     * 更新桶内流体
     */
    static void updateIEBarrelFluid(ItemStack barrelStack, FluidStack fluidStack) {
        CompoundTag tag = barrelStack.getOrCreateTag();

        if (fluidStack == null) {
            tag.remove("tank");
        } else {
            CompoundTag tankTag = new CompoundTag();
            tankTag.putInt("Amount", fluidStack.getAmount());
            ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid());
            if (fluidId != null) {
                tankTag.putString("FluidName", fluidId.toString());
            }
            tag.put("tank", tankTag);
        }
    }
}
