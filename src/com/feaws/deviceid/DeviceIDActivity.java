package com.feaws.deviceid;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DeviceIDActivity extends Activity {

	private static final Uri URI = Uri
			.parse("content://com.google.android.gsf.gservices");
	private static final String ID_KEY = "android_id";

	private String GsfAndroidId = "";
	private String SystemAndroidId = "";
	private String uuid = "";
	private String imei = "";

	public static String getGsfAndroidId(Context context) {
		String params[] = { ID_KEY };
		Cursor c = context.getContentResolver().query(URI, null, null, params,
				null);
		if (!c.moveToFirst() || c.getColumnCount() < 2)
			return null;
		try {
			return Long.toHexString(Long.parseLong(c.getString(1)));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_device_id);
		try {
			TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
			imei = m_telephonyManager.getDeviceId();
		} catch (Exception e) {
		}

		GsfAndroidId = getGsfAndroidId(getApplication());
		SystemAndroidId = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);

		try {
			uuid = UUID.nameUUIDFromBytes(SystemAndroidId.getBytes("utf8"))
					.toString();
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		EditText et1 = (EditText) findViewById(R.id.editText1);
		EditText et2 = (EditText) findViewById(R.id.editText2);
		EditText et3 = (EditText) findViewById(R.id.editText3);
		EditText et4 = (EditText) findViewById(R.id.editText4);
		Button butt = (Button) findViewById(R.id.button1);
		et1.setText(SystemAndroidId);
		et2.setText(GsfAndroidId);
		et3.setText(imei);
		et4.setText(uuid);

		butt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Intent.EXTRA_SUBJECT, "Device Unique IDs");
				intent.putExtra(Intent.EXTRA_TEXT, "Secure ID:   "
						+ SystemAndroidId + "\n" + "GSF ID:   " + GsfAndroidId
						+ "\n+" + "UUID:   " + uuid+ "\n+" + "IMEI:   " + imei
						+ "\n\n\nThanks for using app from @intrepidkarthi(www.intrepidkarthi.com)");
				startActivity(Intent.createChooser(intent,
						"How do you want to share?"));

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.get_device_id, menu);
		return true;
	}

}
