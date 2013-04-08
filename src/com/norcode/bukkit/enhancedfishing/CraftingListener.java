package com.norcode.bukkit.enhancedfishing;

import java.lang.reflect.Field;

import net.minecraft.server.v1_5_R2.ContainerAnvil;
import net.minecraft.server.v1_5_R2.ContainerAnvilInventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import com.norcode.bukkit.enhancedfishing.AnvilCalculator.AnvilResult;

public class CraftingListener implements Listener {

    private EnhancedFishing plugin;

    public CraftingListener(EnhancedFishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (event.getInventory() instanceof AnvilInventory) {
                    AnvilInventory ai = (AnvilInventory) event.getInventory();
                    ItemStack first = ai.getItem(0);
                    ItemStack second = ai.getItem(1);
                    net.minecraft.server.v1_5_R2.ItemStack nmsResult = ((CraftInventoryAnvil)ai).getResultInventory().getItem(0); 
                    ItemStack result = nmsResult == null ? null : CraftItemStack.asCraftMirror(nmsResult); 
                    if (first != null && second != null && result == null) {
                        if (first.getType().equals(Material.FISHING_ROD) && second.getType().equals(Material.ENCHANTED_BOOK)) {
                            ItemStack resultStack = first.clone();
                            ContainerAnvilInventory nmsInv = (ContainerAnvilInventory) ((CraftInventoryAnvil) ai).getInventory();
                            try {
                                Field containerField = ContainerAnvilInventory.class.getDeclaredField("a");
                                containerField.setAccessible(true);
                                ContainerAnvil anvil = (ContainerAnvil) containerField.get(nmsInv);
                                AnvilResult anvilResult = AnvilCalculator.calculateCost(CraftItemStack.asNMSCopy(first), CraftItemStack.asNMSCopy(second));
                                if (anvilResult.getEnchantments() != null) {
                                    anvil.a = anvilResult.getCost();
                                    resultStack.addUnsafeEnchantments(anvilResult.getEnchantments());
                                    ((CraftInventoryAnvil)ai).getResultInventory().setItem(0, CraftItemStack.asNMSCopy(resultStack));
                                }
                                ((CraftPlayer) event.getWhoClicked()).getHandle().setContainerData(anvil, 0, anvil.a);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            
                        }
                    }
                }
            }
        }, 0);
    }
}
