package com.grt192.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class GRTClientSocket extends Thread implements GRTSocket {
	public static final int POLL_TIME = 2;

	private String host;
	private int port;

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	private boolean running;

	private String lastData;

	private Vector<SocketListener> socketListeners;

	public GRTClientSocket(String host, int port) {
		socketListeners = new Vector<SocketListener>();
		this.host = host;
		this.port = port;
		running = false;
	}

	@Override
	public void addSocketListener(SocketListener s) {
		socketListeners.add(s);
	}

	@Override
	public void connect() {
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(1000);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket
					.getOutputStream()));
			notifyConnected();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		try {
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.flush();
				out.close();
				out = null;
			}
			if (socket != null && socket.isConnected()) {
				socket.close();
				socket = null;
				notifyDisconnected();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isConnected() {
		return (socket != null && socket.isConnected());
	}

	@Override
	public void removeSocketListener(SocketListener s) {
		socketListeners.remove(s);
	}

	@Override
	public synchronized void sendData(String data) {
		if (out != null && socket.isConnected()) {
			out.println(data);
			if(out.checkError()){
				System.err.println("outputStream error");
			}
			out.flush();
		}
	}

	public void poll() {
		if ( !isConnected() ){
			connect();
		}
		try {
			String latest = in.readLine();
			if (latest != null && !latest.equals("")) {
				lastData = latest;
				notifyListeners();
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			poll();
		}
	}

	protected void notifyListeners() {
		for (SocketListener s : socketListeners) {
			s.dataRecieved(
					new SocketEvent(this, SocketEvent.ON_DATA, lastData));
		}
	}
	
	protected void notifyConnected() {
		for (SocketListener s : socketListeners) {
			s.onConnect(
					new SocketEvent(this, SocketEvent.ON_CONNECT, null));
		}
	}
	protected void notifyDisconnected() {
		for (SocketListener s : socketListeners) {
			s.onDisconnect(
					new SocketEvent(this, SocketEvent.ON_DISCONNECT, null));
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLastData() {
		return lastData;
	}

	public void halt() {
		running = false;
	}
}
