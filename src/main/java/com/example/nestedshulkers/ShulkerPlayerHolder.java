package com.example.nestedshulkers;

import net.minecraft.world.entity.player.Player;

/** Duck interface so a shulker slot can know which player is using its menu. */
public interface ShulkerPlayerHolder {
    void nestedshulkers$setPlayer(Player player);
}
