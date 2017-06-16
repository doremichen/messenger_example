package com.adam.app.messengerexample;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class RemoteService extends IntentService {

	private Messenger mServiceMessenger = null;

	/**
	 * Constructor description
	 * 
	 * @param name
	 */
	public RemoteService() {
		super("Remote service");
		mServiceMessenger = new Messenger(new Handler() {

			/**
			 * Function Description
			 * 
			 * @param msgFromClient
			 */
			@Override
			public void handleMessage(Message msgFromClient) {
				super.handleMessage(msgFromClient);
				Log.i(Util.TAG, "Remote service get message");
				// Get the message from the client
				Message msgToClient = Message.obtain(msgFromClient);

				switch (msgFromClient.what) {
				case Util.MSG_NUM: {
					Log.i(Util.TAG, "Remote service process message");
					msgToClient.what = Util.MSG_NUM;
					
					// Monitor long operation
					try {
						Thread.sleep(3000L);
						msgToClient.arg2 = msgFromClient.arg1 + msgFromClient.arg2;
						Log.i(Util.TAG, "Remote service start to send message to client");
						msgFromClient.replyTo.send(msgToClient);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Log.w(Util.TAG, "Something interrupt...");
					} catch (RemoteException e) {
						e.printStackTrace();
						Log.w(Util.TAG, "Remote exception happend...");
					}
					
				}
					break;
				}
			}

		});
	}

	/**
	 * Function Description
	 * 
	 * @param intent
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(Util.TAG, "[onHandleIntent] enter");

		
	}

	/**
	 * Function Description
	 * 
	 * @param intent
	 * @return
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(Util.TAG, "[onBind] enter");
		return mServiceMessenger.getBinder();
	}

}

/*
 * ===========================================================================
 * 
 * Revision history
 * 
 * ===========================================================================
 */
