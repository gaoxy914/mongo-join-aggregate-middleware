package middleware;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import conf.Config;
import connection.Server;

public class MiddleWare {
	protected String ip;
	protected int port;
	protected Config config;
	protected Server server;
	
	public MiddleWare(String configPath) throws IOException {
		System.out.println("load Config from : " + configPath);
		config = new Config(configPath);
		if (!config.getLocal()) {
			port = config.getServerPort();
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				System.out.println("ERROR WHILE GETTING LOCAL IP ADDRESS.");
				e.printStackTrace();
			}
			server = new Server(ip, port);
			new Thread(server).start();
		}
	}
}
