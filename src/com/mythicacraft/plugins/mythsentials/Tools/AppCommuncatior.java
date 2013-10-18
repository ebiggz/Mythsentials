package com.mythicacraft.plugins.mythsentials.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class AppCommuncatior extends Thread {

	private final int PORT = 9001;

	public void run() {
		System.out.println("[Mythsentials] AppCommunicator is running.");
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class Handler extends Thread {

		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {

				//Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				Mythsentials.clients.add(out);
				System.out.println("[Mythsentials] An AdminMythica client connected. (" + socket.getInetAddress().getHostAddress() + ")");

				while (true) {
					String input = in.readLine();
					if (input == null) {
						System.out.println("[Mythsentials] An AdminMythica client disconnected.");
						Mythsentials.clients.remove(out);
						try {
							socket.close();
						} catch (IOException e) {
						}
						return;
					}
					if(out.checkError()) {
						System.out.println("[Mythsentials] An AdminMythica client disconnected.");
						Mythsentials.clients.remove(out);
						try {
							socket.close();
						} catch (IOException e) {
						}
						return;
					}

					//for (PrintWriter writer : writers) {

					//}
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
