
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.support.v4.content.FileProvider;

class UDPClient
{
	public static void main(String args[]) throws Exception
	{
		File file = new File("C:\\Users\\lgallo\\workspace\\talk-to-home\\test1_AMR-NB_Mono_7.95kbps_8000Hz.3gp");
		FileInputStream fis = new FileInputStream(file);
		
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData());
		
		System.out.println("FROM SERVER:" + modifiedSentence);
		clientSocket.close();
	}
}
