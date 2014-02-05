package com.duam.talktohome;

import static com.duam.talktohome.ConstantesTalkToHome.AUX_FILE_NAME;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
//import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private static final String TAG = MainActivity.class.getName();
	private static String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ AUX_FILE_NAME;

	private MediaRecorder recorder = null;
//	private MediaPlayer player = null;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);
		
		final Button btnRec = (Button) findViewById(R.id.btnRec);
		btnRec.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                	btnRec.setBackgroundColor(Color.RED);
                	startRecording();
                	
                	return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                	btnRec.setBackgroundResource(R.drawable.custom_btn_opal);
                	stopRecording();
                	uploadAndDelete();
                	
                	return true;
                }
                else
                {
                    return false;
                }
            }
        });		
	}

	private void uploadAndDelete()
	{
		final ProgressDialog pd = ProgressDialog.show(this, "Subiendo mensaje al servidor", "Aguarde un momento, por favor");
		
		UploadAudioTask task = new UploadAudioTask()
		{
			@Override
			protected void onPreExecute() 
			{
				pd.show();
			}
			@Override
			protected void onPostExecute(Exception error)
			{
				File file = new File(fileName);
		        file.delete();
		        pd.dismiss();
		        
				if (error != null)
				{
					new AlertDialog.Builder(MainActivity.this).setTitle("Error al enviar mensaje").setMessage(error.getMessage())
					.setNeutralButton("Lo entiendo", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
						}
					}).show();
				}
				else
				{
					Toast toast = Toast.makeText(MainActivity.this, "Mensaje enviado con éxito", Toast.LENGTH_LONG);
					toast.show();
				}
			}
		};
		task.execute(fileName);
	}
	
//	private void startPlaying()
//	{
//		player = new MediaPlayer();
//		try
//		{
//			player.setDataSource(fileName);
//			player.prepare();
//			player.start();
//		}
//		catch (IOException e)
//		{
//			Log.e(TAG, "prepare() failed");
//		}
//	}
//
//	private void stopPlaying()
//	{
//		player.release();
//		player = null;
//	}

	private void startRecording()
	{
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setOutputFile(fileName);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try
		{
			recorder.prepare();
		}
		catch (IOException e)
		{
			Log.e(TAG, "prepare() failed");
		}

		recorder.start();
	}

	private void stopRecording()
	{
		recorder.stop();
		recorder.release();
		recorder = null;
	}

//	class RecordButton extends Button
//	{
//		boolean mStartRecording = true;
//
//		OnClickListener clicker = new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				onRecord(mStartRecording);
//				if (mStartRecording)
//				{
//					setText("Stop recording");
//				}
//				else
//				{
//					setText("Start recording");
//				}
//				mStartRecording = !mStartRecording;
//			}
//		};
//
//		public RecordButton(Context ctx)
//		{
//			super(ctx);
//			setText("Start recording");
//			setOnClickListener(clicker);
//		}
//	}
//
//	class PlayButton extends Button
//	{
//		boolean mStartPlaying = true;
//
//		OnClickListener clicker = new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				onPlay(mStartPlaying);
//				if (mStartPlaying)
//				{
//					setText("Stop playing");
//				}
//				else
//				{
//					setText("Start playing");
//				}
//				mStartPlaying = !mStartPlaying;
//			}
//		};
//
//		public PlayButton(Context ctx)
//		{
//			super(ctx);
//			setText("Start playing");
//			setOnClickListener(clicker);
//		}
//	}
//
//	@Override
//	public void onPause()
//	{
//		super.onPause();
//		if (recorder != null)
//		{
//			recorder.release();
//			recorder = null;
//		}
//
//		if (player != null)
//		{
//			player.release();
//			player = null;
//		}
//	}
}
