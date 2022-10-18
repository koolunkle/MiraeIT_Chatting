package kr.or.mrhi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class ChatServerThread extends Thread {

	private String nickname;
	private Socket socket;
	private Vector<PrintWriter> printWriterList;

	public ChatServerThread(Socket socket, Vector<PrintWriter> printWriterList) {
		this.nickname = null;
		this.socket = socket;
		this.printWriterList = printWriterList;
	}

	@Override
	public void run() {
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter printWriter = null;
		
		try {
			inputStream = this.socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);

			outputStream = this.socket.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			printWriter = new PrintWriter(outputStreamWriter);

			while (true) {
				String messageReceive = bufferedReader.readLine();

				if (messageReceive == null) {
					System.out.println("클라이언트가 연결을 종료했습니다. " + "클라이언트 IP: "
							+ this.socket.getInetAddress().getHostAddress() + ", 클라이언트 포트번호: " + this.socket.getPort());

					synchronized (printWriterList) {
						printWriterList.remove(printWriter);
					}

					String messageDisconnect = this.nickname + "님이 퇴장했습니다.";
					sendMessage(messageDisconnect);

					break;
				}

				String[] tokens = messageReceive.split(":");

				if ("join".equals(tokens[0])) {
					this.nickname = tokens[1];

					synchronized (printWriterList) {
						printWriterList.add(printWriter);
					}

					String messageJoin = this.nickname + "님이 입장했습니다.";
					System.out.println(messageJoin);
					sendMessage(messageJoin);
				} else if ("message".equals(tokens[0])) {
					String messageSend = this.nickname + ": " + tokens[1];
					sendMessage(messageSend);
				} else if ("quit".equals(tokens[0])) {
					synchronized (printWriterList) {
						printWriterList.remove(printWriter);
					}

					String messageQuit = this.nickname + "님이 퇴장했습니다.";
					sendMessage(messageQuit);

					break;
				}
			}
		} catch (IOException e) {
			System.out.println(this.nickname + "의 소켓이 이상이 있어서 종료되었습니다.");
		} finally {
			if (this.socket != null && !this.socket.isClosed()) {
				try {
					this.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			synchronized (printWriterList) {
				printWriterList.remove(printWriter);
			}
		}
	} // end of run

	private void sendMessage(String message) {
		synchronized (printWriterList) {
			for (PrintWriter pw : printWriterList) {
				pw.println(message);
				pw.flush();
			}
		}
	}

} // end of class