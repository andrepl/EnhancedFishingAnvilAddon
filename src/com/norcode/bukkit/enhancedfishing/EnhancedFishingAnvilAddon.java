package com.norcode.bukkit.enhancedfishing;

import org.bukkit.plugin.java.JavaPlugin;

public class EnhancedFishingAnvilAddon extends JavaPlugin {
    @Override
    public void onEnable() {
        EnhancedFishing plugin = (EnhancedFishing) getServer().getPluginManager().getPlugin("EnhancedFishing");
        getServer().getPluginManager().registerEvents(new CraftingListener(plugin), this);
    }
}
