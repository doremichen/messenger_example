package com.adam.app.messengerexample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	/*
	 * ui widget handler
	 */
	private TextView mTvConnectStatus;
	private LinearLayout mContainer;

	// Remote service handler
	private Messenger mService;
	private boolean mIsConnected;

	// Count
	private int mCount;

	/**
	 * Recieve the message from the remote service and process it
	 */
	private Messenger mMessenger = new Messenger(new Handler() {

		/**
		 * Function Description
		 * 
		 * @param msg
		 */
		@Override
		public void handleMessage(Message msgFromServer) {
			super.handleMessage(msgFromServer);
			Log.i(Util.TAG, "[handleMessage] Client enter");

			switch (msgFromServer.what) {
			case Util.MSG_NUM: {
				TextView tvShow = (TextView) mContainer
						.findViewById(msgFromServer.arg1);
				tvShow.setText(tvShow.getText() + "==>" + msgFromServer.arg2);
			}
				break;
			}
		}

	});

	private ServiceConnection mConnect = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(Util.TAG, "[onServiceConnected] UI connect service");
			mIsConnected = true;
			// Instance service proxy
			mService = new Messenger(service);

			mTvConnectStatus.setText("Connected");

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(Util.TAG, "[onServiceConnected] UI disconnect service");
			mIsConnected = false;
			mService = null;

			mTvConnectStatus.setText("Disconnected");
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Util.TAG, "[onCreate] UI enter");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get text view handler
		this.mTvConnectStatus = (TextView) this
				.findViewById(R.id.tv_connect_status);
		this.mContainer = (LinearLayout) this
				.findViewById(R.id.LayoutContainer);

		// Binder the remote service
		binderRemoteService();

	}

	/**
	 * Function Description
	 * 
	 */
	@Override
	protected void onDestroy() {
		Log.i(Util.TAG, "[onDestroy] UI enter");
		super.onDestroy();
		this.unbindService(mConnect);
	}

	public void onBtnAdd(View v) {
		Log.i(Util.TAG, "[onBtnAdd] enter");

		int value1 = mCount++;

		int value2 = (int) (Math.random() * 100);

		// New text view to show the message from the remote service
		TextView tvShow = new TextView(MainActivity.this);
		tvShow.setText(value1 + " + " + value2 + " = calculation... ");
		tvShow.setId(value1);

		this.mContainer.addView(tvShow);

		// Packet the message and send to the remote service
		Message msgFromClient = Message.obtain(null, Util.MSG_NUM, value1,
				value2);
		msgFromClient.replyTo = mMessenger;

		if (mIsConnected == true) {
			// Send message to the remote service
			try {
				mService.send(msgFromClient);
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.i(Util.TAG, "Remote service happen remote exception.");
			}
		}

	}

	/**
	 * 
	 * Function Description
	 * 
	 */
	private void binderRemoteService() {
		Log.i(Util.TAG, "[binderRemoteService] UI enter");
		Intent it = new Intent();
		it.setAction("com.adam.app.service.calculate");
		this.bindService(it, mConnect, Context.BIND_AUTO_CREATE);

	}

}
