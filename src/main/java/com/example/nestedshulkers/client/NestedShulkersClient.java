package com.example.nestedshulkers.client;

import com.example.nestedshulkers.mixin.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class NestedShulkersClient implements ClientModInitializer {

    private static KeyMapping copyKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("nestedshulkers", "main"));
        copyKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.nestedshulkers.copy_command", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, category));

        // 1) Tooltip hint (tooltips can't be clicked, so this advertises the hotkey).
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (Minecraft.getInstance().gui.screen() instanceof AbstractContainerScreen<?>) {
                lines.add(Component.literal("[" + copyKey.getTranslatedKeyMessage().getString() + "] Copy as /give command")
                    .withStyle(ChatFormatting.DARK_GRAY));
            }
        });

        // 2) Hovering any slot (including the hotbar row) in an inventory screen.
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof AbstractContainerScreen<?> container) {
                ScreenKeyboardEvents.allowKeyPress(screen).register((s, keyEvent) -> {
                    if (copyKey.matches(keyEvent)) {
                        if (s.getFocused() instanceof EditBox) return true; // typing in search/rename box
                        Slot slot = ((AbstractContainerScreenAccessor) container).nestedshulkers$getHoveredSlot();
                        if (slot != null && slot.hasItem()) {
                            copyGive(client, slot.getItem());
                            return false;
                        }
                    }
                    return true;
                });
            }
        });

        // 3) No screen open: copies the item in your hand.
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (copyKey.consumeClick()) {
                if (mc.gui.screen() == null && mc.player != null) {
                    copyGive(mc, mc.player.getMainHandItem());
                }
            }
        });
    }

    private static void copyGive(Minecraft mc, ItemStack stack) {
        if (stack.isEmpty() || mc.level == null || mc.player == null) return;

        RegistryOps<Tag> ops = mc.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Optional<Tag> encoded = ItemStack.CODEC.encodeStart(ops, stack).result();
        if (encoded.isEmpty() || !(encoded.get() instanceof CompoundTag tag)) {
            mc.player.sendSystemMessage(Component.literal("Couldn't encode that item."));
            return;
        }

        String id = tag.getStringOr("id", "minecraft:air");
        int count = tag.getIntOr("count", 1);

        StringBuilder cmd = new StringBuilder("/give @s ").append(id);
        CompoundTag comps = tag.getCompound("components").orElse(null);
        if (comps != null && !comps.isEmpty()) {
            cmd.append('[');
            boolean first = true;
            for (String key : comps.keySet()) {
                if (!first) cmd.append(',');
                first = false;
                if (key.startsWith("!")) {
                    cmd.append(key);                       // removed default component
                } else {
                    cmd.append(key).append('=').append(comps.get(key)); // SNBT
                }
            }
            cmd.append(']');
        }
        cmd.append(' ').append(count);

        mc.keyboardHandler.setClipboard(cmd.toString());
        mc.player.sendSystemMessage(
            Component.literal("Copied /give command (" + cmd.length() + " chars)"));
    }
}
