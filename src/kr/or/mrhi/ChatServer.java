package kr.or.mrhi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class ChatServer {

	public static final int PORT = 1126;

	public static void main(String[] args) {
		String ip = null;

		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		ServerSocket serverSocket = null;

		Vector<PrintWriter> printWriterList = new Vector<>();

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(ip, PORT));
			System.out.println("[연결을 기다립니다.] " + Thread.currentThread().getId() + "의 IP: " + ip + ", "
					+ Thread.currentThread().getId() + "의 포트번호: " + PORT);

			while (true) {
				Socket socket = serverSocket.accept();

				ChatServerThread chatServerThread = new ChatServerThread(socket, printWriterList);
				chatServerThread.start();

				SocketAddress socketAddress = socket.getRemoteSocketAddress();
				InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
				System.out.println("[연결을 수락합니다.] " + "클라이언트의 IP: " + inetSocketAddress.getHostString()
						+ ", 클라이언트의 포트번호: " + inetSocketAddress.getPort());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}