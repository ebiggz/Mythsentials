package com.mythicacraft.plugins.mythsentials.MiscCommands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class ForumConnector implements Runnable {

	private String player;
	private String email;
	private String password;

	public ForumConnector(String player, String email, String password){
		this.player = player;
		this.email = email;
		this.password = password;
	}

	@Override
	public void run() {
		try {
			URL phpUrl = new URL("http://www.mythicacraft.com/game2forum.php?username=" + player + "&password=" + password + "&email=" + email);
			URLConnection urlCon = phpUrl.openConnection();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							urlCon.getInputStream()));
			@SuppressWarnings("unused")
			String line;

			while ((line = br.readLine()) != null)
				br.close();
		} catch(Exception e) {
			// handle errors here...
		}
	}
}
