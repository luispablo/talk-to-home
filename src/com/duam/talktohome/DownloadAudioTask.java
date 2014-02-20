package com.duam.talktohome;

import static com.duam.talktohome.ConstantesTalkToHome.SERVER_PORT;
import static com.duam.talktohome.ConstantesTalkToHome.PACKET_SIZE;
import static com.duam.talktohome.ConstantesTalkToHome.AUX_FILE_NAME;
import static com.duam.talktohome.ConstantesTalkToHome.OK_MESSAGE;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadAudioTask extends AsyncTask<DatagramSocket, Void, Void> 
{
	private static final String TAG = DownloadAudioTask.class.getName();
	
	@Override
	protected Void doInBackground(DatagramSocket... params) 
	{
		DatagramSocket serverSocket = params[0];
		
		try 
		{	
			byte[] bytes = new byte[PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(bytes, bytes.length);
			Log.d(TAG, "Listening on port "+ SERVER_PORT);

			serverSocket.receive(receivePacket);
			long size = Long.parseLong(new String(receivePacket.getData()).trim());

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ AUX_FILE_NAME));
			
			byte[] response = OK_MESSAGE.getBytes(); 
			
			int count = 0;
			
			while (count < size)
			{				
				bytes = new byte[(size > PACKET_SIZE) ? PACKET_SIZE : (int) size];
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				Log.d(TAG, "About to receive chunk...");
				serverSocket.receive(packet);
				bos.write(bytes);
		
				InetAddress IPAddress = packet.getAddress();
				int port = packet.getPort();
				
				Log.d(TAG, "Sending response...");
				DatagramPacket responsePacket = new DatagramPacket(response, response.length, IPAddress, port);
				serverSocket.send(responsePacket);
				
				count += bytes.length;
				long remaining = size - count;

				if (remaining > 0)
				{
					bytes = new byte[(remaining < PACKET_SIZE) ? (int) remaining : PACKET_SIZE];
				}
			}
			bos.flush();
			bos.close();			
		} 
		catch (IOException e) 
		{
			Log.e(TAG, "Socket comm error", e);
		}
		finally
		{
			serverSocket.close();
		}
		
		return null;
	}

}
