package com.mythicacraft.plugins.mythsentials.GUIAPI;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class GUIManager {

	//storage for players in guis
	private HashMap<Player,GUI> openGUIs = new HashMap<Player,GUI>();
	private HashMap<GUI,Inventory> guiInvs = new HashMap<GUI,Inventory>();

	protected GUIManager() { /*exists to block instantiation*/ }
	private static GUIManager instance = null;
	public static GUIManager getInstance() {
		if(instance == null) {
			instance = new GUIManager();
		}
		return instance;
	}

	//present a gui on players screen
	public void showGUI(GUI gui, Player player) {
		if(gui == null) return;
		if(player == null) return;
		player.closeInventory();
		openGUIs.put(player, gui);
		Inventory inv = gui.createInventory(player);
		guiInvs.put(gui, inv);
		player.openInventory(inv);
	}

	//used by GUIListener to remove players from GUI storage
	void playerClosedGUI(Player player) {
		guiInvs.remove(getPlayersCurrentGUI(player));
		openGUIs.remove(player);
	}

	public Inventory getGUIsCurrentInv(GUI gui) {
		if(guiInvs.containsKey(gui)) {
			return guiInvs.get(gui);
		} else {
			return null;
		}
	}

	public GUI getPlayersCurrentGUI(Player player) {
		if(openGUIs.containsKey(player)) {
			return openGUIs.get(player);
		} else {
			return null;
		}
	}


	public boolean playerHasGUIOpen(Player player) {
		return (openGUIs.containsKey(player));
	}
}
