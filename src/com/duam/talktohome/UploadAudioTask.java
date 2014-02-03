package com.duam.talktohome;

import static com.duam.talktohome.ConstantesTalkToHome.SERVER_HOST;
import static com.duam.talktohome.ConstantesTalkToHome.SERVER_PORT;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.AsyncTask;
import android.util.Log;

public class UploadAudioTask extends AsyncTask<String, Void, Void>
{
	private static final String TAG = UploadAudioTask.class.getName();

	@Override
	protected Void doInBackground(String... params)
	{
		try
		{
			byte[] data = IOUtil.readFile(params[0]);
			
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(SERVER_HOST);
			
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, SERVER_PORT);
			clientSocket.send(sendPacket);			
			clientSocket.close();
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Error enviando la info", ex);
		}

		return null;
	}
}
