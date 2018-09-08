package connection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class OutConnections {
	private HashMap<String, Socket> connections;
	
	public OutConnections() {
		connections = new HashMap<String, Socket>();
	}
	
	public void insert(String ip, int port, Socket socket) {
		String key = ip + "." + port;
		connections.put(key, socket);
	}
	
	public Socket getSocket(String ip, int port) {
		String key = ip + "." + port;
		return connections.get(key);
	}
	
	public void sendData(String ip, int port, String message) {
		System.out.println("send message : " + message + " to LowerMiddleWare : " + ip);
		String key = ip + "." + port;
		Socket socket;
		if (connections.containsKey(key)) {
			socket = connections.get(key);
			sendData(socket, message);
		} else {
			try {
				socket = new Socket(ip, port);
				connections.put(key, socket);
				sendData(socket, message);
			} catch (UnknownHostException e) {
				System.out.println("ERROR WHILE CREATING SOCKET.");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("ERROR WHILE CREATING SOCKET.");
				e.printStackTrace();
			}
		}
	}
	
	private void sendData(Socket socket, String message) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(message);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			System.out.println("ERROR WHILE CREATING BUFFEREDWRITER.");
			e.printStackTrace();
		}
	}
	
	public void close() {
		for (Socket socket : connections.values()) {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("ERROR WHILE CLOSING OUTCONNECTIONS.");
				e.printStackTrace();
			}
		}
	}
}
