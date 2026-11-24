package com.example.nestedshulkers.mixin;

import com.example.nestedshulkers.ShulkerPlayerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public abstract class ShulkerBoxSlotMixin implements ShulkerPlayerHolder {

    @Unique
    private Player nestedshulkers$player;

    @Override
    public void nestedshulkers$setPlayer(Player player) {
        this.nestedshulkers$player = player;
    }

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void nestedshulkers$allowNesting(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (nestedshulkers$player != null
                && nestedshulkers$player.isCreative()
                && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock) {
            cir.setReturnValue(true);
        }
    }
}
