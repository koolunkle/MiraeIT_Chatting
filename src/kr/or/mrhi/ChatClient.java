package kr.or.mrhi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {

	public static final String IP_SERVER = "172.30.1.38";
	public static final int PORT_SERVER = 1126;
	public static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		Socket socket = null;

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter printWriter = null;

		String nickname = null;

		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(IP_SERVER, PORT_SERVER));
			System.out.println("[채팅방에 입장했습니다.]");

			while (true) {
				System.out.print("[닉네임을 입력하세요.]: ");
				nickname = scanner.nextLine();

				if (nickname.isEmpty() == false) {
					break;
				}

				System.out.println("[닉네임은 공백 없이 한 글자 이상 입력하세요.]");
			}
			scanner.close();

			outputStream = socket.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			printWriter = new PrintWriter(outputStreamWriter, true);

			String nicknameConvert = "join:" + nickname + "\r\n";
			printWriter.println(nicknameConvert);

			ChatClientWindow chatClientWindow = new ChatClientWindow(nickname, socket);
			chatClientWindow.show();
		} catch (IOException e) {
			System.out.println("[서버 측에 문제가 발생했습니다.]");
		}
	}

}