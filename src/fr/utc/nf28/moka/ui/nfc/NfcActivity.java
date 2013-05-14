package fr.utc.nf28.moka.ui.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import fr.utc.nf28.moka.MainActivity;
import fr.utc.nf28.moka.R;
import fr.utc.nf28.moka.util.NfcUtils;

import java.io.UnsupportedEncodingException;

import static fr.utc.nf28.moka.util.LogUtils.makeLogTag;

public class NfcActivity extends Activity {

	private static final String TAG = makeLogTag(NfcActivity.class);

	private NfcAdapter mNfcAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_activity);

		findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(NfcActivity.this, MainActivity.class));
				finish();
			}
		});

		mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		if(mNfcAdapter == null){
			((TextView)findViewById(R.id.info)).setText(R.string.info_no_nfc_text);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		enableNfcDiscovering();
		if (getIntent().hasExtra(NfcAdapter.EXTRA_TAG) && getIntent().getAction().equals(NfcAdapter.EXTRA_TAG)) {
			processTag((Tag) getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		disableNfcDiscovering();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
			processTag((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
		}
	}

	/**
	 * let the activity to claim priority on tag_discover event when
	 * the activity is displayed
	 */
	private void enableNfcDiscovering() {
		if(mNfcAdapter!=null){
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
			mNfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{tagDetected}, null);
		}
	}

	/**
	 * restore nfc priority
	 */
	private void disableNfcDiscovering() {
		if(mNfcAdapter!=null){
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	/**
	 * process tag which has launched this activity or which has been caught;
	 *
	 * @param tag tag from intent
	 */
	private void processTag(Tag tag) {
		try {
			int result = NfcUtils.writeMokaTag(tag, "http://moka.fr/q/androidap/lolowned", false);
			Log.i(TAG, "write : " + String.valueOf(result));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}