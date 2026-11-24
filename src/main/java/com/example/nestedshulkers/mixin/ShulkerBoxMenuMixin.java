package com.example.nestedshulkers.mixin;

import com.example.nestedshulkers.ShulkerPlayerHolder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxMenu.class)
public abstract class ShulkerBoxMenuMixin extends AbstractContainerMenu {

    protected ShulkerBoxMenuMixin(MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    // Runs on the client (prediction) and on the server (authoritative), each with their own player.
    @Inject(
        method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",
        at = @At("RETURN")
    )
    private void nestedshulkers$tagSlots(int containerId, Inventory playerInventory, Container container, CallbackInfo ci) {
        for (Slot slot : this.slots) {
            if (slot instanceof ShulkerPlayerHolder holder) {
                holder.nestedshulkers$setPlayer(playerInventory.player);
            }
        }
    }
}
