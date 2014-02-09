package com.mythicacraft.plugins.mythsentials;



public class MythianManager {

	/*private Mythsentials plugin;

	public MythianManager(Mythsentials plugin) {
		this.plugin = plugin;
	}
	 */

	public Mythian getMythian(String playerName) {
		return new Mythian(playerName);
	}
}
