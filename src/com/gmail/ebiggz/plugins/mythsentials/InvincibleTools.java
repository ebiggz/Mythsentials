package com.gmail.ebiggz.plugins.mythsentials;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InvincibleTools implements Listener{
	public static Mythsentials plugin;
	 
	public InvincibleTools(Mythsentials instance) {
		plugin = instance;
	}
    	@SuppressWarnings("deprecation")
    	@EventHandler(priority = EventPriority.MONITOR)
    	public void onPlayerInteract(PlayerInteractEvent event) {
    		// Skip if not using an item
    		if (!event.hasItem()) return;
    		// Skip block place
    		if (event.isBlockInHand()) return;
    		
    		Player player = event.getPlayer();
            if (player.getItemInHand().getDurability() >= plugin.toolRepairPoint) {
                if(player.hasPermission("mythica.invtools")) {
                    int itemInHand = player.getItemInHand().getTypeId();
                    if (plugin.tools.containsKey(itemInHand)) {
                        // Tool is invincible. Set damage to 0.
                        player.getItemInHand().setDurability((short)-1);
                        player.updateInventory();
                    }
                }
            }   		
    	}
   
    	@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.MONITOR)
        public void onEntityDamage(EntityDamageEvent event) {
            if (!(event.getEntity() instanceof Player)) return;
            Player player = (Player)event.getEntity();
            if (!player.hasPermission("mythica.invarmor")) return;         
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item.getDurability() < plugin.armorRepairPoint) continue;
                if (plugin.armor.containsKey(item.getTypeId())) {
                    item.setDurability((short)-1);
                    player.updateInventory();
                }
           }
      }
}
