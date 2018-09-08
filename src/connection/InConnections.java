package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;


public class InConnections {
	private HashMap<String, Socket> connections;
	
	public InConnections() {
		connections = new HashMap<String, Socket>();
	}
	
	public void insert(Socket socket, Vector<String> jobQueue) {
		String key = socket.getInetAddress().toString() + "." + socket.getPort();
		connections.put(key, socket);
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ThreadListeningHandler handler = new ThreadListeningHandler(bufferedReader, jobQueue);
			new Thread(handler).start();
		} catch (IOException e) {
			System.out.println("ERROR WHILE CREATING BUFFEREDREADER.");
			e.printStackTrace();
		}
	}
	
	public void close() {
		for (Socket socket : connections.values()) {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("ERROR WHILE CLOSING INCONNECTIONS.");
				e.printStackTrace();
			}
		}
	}
}

class ThreadListeningHandler implements Runnable {
	BufferedReader bufferedReader;
	Vector<String> queue;
	
	
	public ThreadListeningHandler(BufferedReader bufferedReader, Vector<String> queue) {
		this.bufferedReader = bufferedReader;
		this.queue = queue;
	}
	
	public void run() {
		while (bufferedReader != null) {
			String line = null;
			try {
				line = bufferedReader.readLine();
				if (line != null) {
					System.out.println("receive : " + line);
					queue.add(line);
				}
			} catch (IOException e) {
				try {
					bufferedReader.close();	
					bufferedReader = null;
				} catch (IOException e1) {
					System.out.println("ERROR WHILE CLOSING BUFFER.");
				}
			}
		}
	}
}