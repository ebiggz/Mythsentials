package com.mythicacraft.plugins.mythsentials.Pets;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class PetCmdProperties {

	private Type type;
	private Player newOwner;
	private OfflinePlayer newOwnerO;
	private boolean useOfflineP;

	public PetCmdProperties(Type type) {
		this.type = type;
	}

	public PetCmdProperties(Type type, Player newOwner) {
		this.newOwner = newOwner;
		this.type = type;
	}

	public PetCmdProperties(Type type, OfflinePlayer newOwner) {
		this.newOwnerO = newOwner;
		this.type = type;
		useOfflineP = true;
	}

	public PetCmdProperties(Type type, List<String> guests) {
		this.type = type;
	}

	public enum Type {
		GIVE, INFO
	}

	//getter and setters
	public Type getType() {
		return type;
	}

	public Player getNewOwner() {
		return newOwner;
	}

	public OfflinePlayer getNewOwnerO() {
		return newOwnerO;
	}

	public boolean useOfflinePlayer(){
		return useOfflineP;
	}
}
