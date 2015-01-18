package com.mythicacraft.plugins.mythsentials.GUIAPI;

import java.util.HashMap;

import org.bukkit.entity.Player;


public class GUIManager {

	//storage for players in guis
	private HashMap<Player,GUI> openGUIs = new HashMap<Player,GUI>();

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
		openGUIs.put(player, gui);
		player.openInventory(gui.createInventory(player));
	}

	//used by GUIListener to remove players from GUI storage
	void playerClosedGUI(Player player) {
		openGUIs.remove(player);
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
