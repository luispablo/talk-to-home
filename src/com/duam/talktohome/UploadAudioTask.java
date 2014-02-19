package com.duam.talktohome;

import static com.duam.talktohome.ConstantesTalkToHome.SERVER_HOST;
import static com.duam.talktohome.ConstantesTalkToHome.SERVER_PORT;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class UploadAudioTask extends AsyncTask<String, Void, Exception>
{
	private static final String TAG = UploadAudioTask.class.getName();

	private static final int PACKET_SIZE = 1024;
	private static final String OK_MESSAGE = "OK";
	private static final String AUX_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() +"/aux_audio_file.3gp";

	@Override
	protected Exception doInBackground(String... params)
	{
		Log.d(TAG, "About to upload file...");
		
		try
		{
			File file = new File(params[0]);
			String fileLength = String.valueOf(file.length());
			
			byte[] bytes = new byte[1024];
			
			FileInputStream fis = new FileInputStream(file);
			
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(5000);
			InetAddress address = InetAddress.getByName(SERVER_HOST);

			// inform file size
			clientSocket.send(new DatagramPacket(fileLength.getBytes(), fileLength.getBytes().length, address, SERVER_PORT));
			Log.d(TAG, "File length sent");
			
			byte[] response = new byte[256];
			
			while (fis.read(bytes) >= 0)
			{
				DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, address, SERVER_PORT);
				clientSocket.send(sendPacket);

				DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, SERVER_PORT);
				clientSocket.receive(responsePacket);
			}
			Log.d(TAG, "File content sent");
			
			fis.close();
			
			receiveResponse(clientSocket);
			
			clientSocket.close();
		}
		catch (SocketTimeoutException ex)
		{
			return ex;
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Error enviando la info", ex);
		}

		return null;
	}
	
	private void receiveResponse(DatagramSocket socket)
	{
		Log.d(TAG, "Starting to receive response...");
		try
		{
			byte[] bytes = new byte[PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(bytes, bytes.length);
			
			socket.setSoTimeout(40000);
			Log.d(TAG, "About to receive file size...");
			socket.receive(receivePacket);
			Log.d(TAG, "received!");
			long fileSize = Long.parseLong(new String(receivePacket.getData()).trim());
			
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(AUX_FILE_NAME));
			
			byte[] response = OK_MESSAGE.getBytes(); 
			
			int count = 0;
			
			while (count < fileSize)
			{				
				bytes = new byte[(fileSize > PACKET_SIZE) ? PACKET_SIZE : (int) fileSize];
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				Log.d(TAG, "About to receive chunk...");
				socket.receive(packet);
				bos.write(bytes);
		
				InetAddress IPAddress = packet.getAddress();
				int port = packet.getPort();
				
				Log.d(TAG, "Sending response...");
				DatagramPacket responsePacket = new DatagramPacket(response, response.length, IPAddress, port);
				socket.send(responsePacket);
				
				count += bytes.length;
				long remaining = fileSize - count;

				if (remaining > 0)
				{
					bytes = new byte[(remaining < PACKET_SIZE) ? (int) remaining : PACKET_SIZE];
				}
			}
			bos.flush();
			bos.close();
			Log.d(TAG, "finished!");
			
			Log.d(TAG, "About to play file");
			MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(AUX_FILE_NAME);
            mp.prepare();
            mp.start();			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}				
	}
	
}
