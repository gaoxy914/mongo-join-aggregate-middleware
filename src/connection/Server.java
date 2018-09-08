package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements Runnable {
	private String hostIp;
	private int hostPort;
	private ServerSocket serverSocket;
	private Vector<String> queue;
	private OutConnections outConnections = new OutConnections();
	private InConnections inConnections = new InConnections();
	private boolean running;
	
	public Server(String ip, int port) {
		hostIp = ip;
		hostPort = port;
		try {
			serverSocket = new ServerSocket(hostPort);
			System.out.println("Server Starts" + " at localIp : " + hostIp + " at localPort : " + hostPort);
		} catch (IOException e) {
			System.out.println("ERROR WHILE CREATING SERVERSOCKET.");
			e.printStackTrace();
		}
		queue = new Vector<String>();
		running = true;
	}
	
	public void sendData(String ip, int port, String message) {
		outConnections.sendData(ip, port, message);
	}
	
	public int getJobQueueSize() {
		return queue.size();
	}
	
	public Vector<String> getJobQueue() {
		return queue;
	}
	
	public void close() {
		if (running) {
			running = false;
		}
		inConnections.close();
		outConnections.close();
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				Socket socket = serverSocket.accept();
				inConnections.insert(socket, queue);
			} catch (IOException e) {
				System.out.println("ERROR WHILE ACCEPTING A CLIENT SOCKET.");
				e.printStackTrace();
			}
		}
	}
}
