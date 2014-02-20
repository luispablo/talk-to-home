package com.duam.talktohome;

import static com.duam.talktohome.ConstantesTalkToHome.SERVER_HOST;
import static com.duam.talktohome.ConstantesTalkToHome.SERVER_PORT;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class UploadAudioTask extends AsyncTask<String, Void, DatagramSocket>
{
	private static final String TAG = UploadAudioTask.class.getName();

	private static final int PACKET_SIZE = 1024;
	private static final String OK_MESSAGE = "OK";
	private static final String AUX_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() +"/aux_audio_file.3gp";

	@Override
	protected DatagramSocket doInBackground(String... params)
	{
		Log.d(TAG, "About to upload file...");
		
		try
		{
			File file = new File(params[0]);
			String fileLength = String.valueOf(file.length());
			
			byte[] bytes = new byte[1024];
			
			FileInputStream fis = new FileInputStream(file);
			
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(50000);
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
			
			return clientSocket;
		}
		catch (SocketTimeoutException ex)
		{
			Log.e(TAG, "Error enviando la info", ex);
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Error enviando la info", ex);
		}

		return null;
	}
	
}
