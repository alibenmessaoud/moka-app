package fr.utc.nf28.moka.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.utc.nf28.moka.R;
import fr.utc.nf28.moka.data.HistoryEntry;
import fr.utc.nf28.moka.io.MokaRestAdapter;
import fr.utc.nf28.moka.io.MokaRestService;
import fr.utc.nf28.moka.io.receiver.MokaReceiver;
import fr.utc.nf28.moka.io.receiver.RefreshHistoryReceiver;
import fr.utc.nf28.moka.util.SharedPreferencesUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static fr.utc.nf28.moka.util.LogUtils.makeLogTag;

public class HistoryEntryListFragment extends SherlockFragment implements RefreshHistoryReceiver.OnRefreshHistoryListener,
		Callback<List<HistoryEntry>> {
	private static final String DEFAULT_REST_SERVER_IP = "192.168.1.6";
	private static final String TAG = makeLogTag(HistoryEntryListFragment.class);
	private HistoryItemAdapter mAdapter;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private RefreshHistoryReceiver mRefreshHistoryReceiver;
	private MokaRestService mMokaRestService;

	public HistoryEntryListFragment() {
	}

	public static HistoryEntryListFragment newInstance() {
		return new HistoryEntryListFragment();
	}

	// Fragment lifecycle management

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_history_item_list, container, false);

		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);

		mListView = (ListView) rootView.findViewById(android.R.id.list);
		mListView.setEmptyView(rootView.findViewById(android.R.id.empty));
		mAdapter = new HistoryItemAdapter(getSherlockActivity());
		mListView.setAdapter(mAdapter);

		// Launch the background task to retrieve history entries from the RESTful server
		final String API_URL = "http://" + PreferenceManager.getDefaultSharedPreferences(getSherlockActivity())
				.getString(SharedPreferencesUtils.KEY_PREF_IP, DEFAULT_REST_SERVER_IP) + "/moka";
		mMokaRestService = MokaRestAdapter.getInstance(API_URL).create(MokaRestService.class);
		mRefreshHistoryReceiver = new RefreshHistoryReceiver(this);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshHistory();
		LocalBroadcastManager.getInstance(getSherlockActivity()).registerReceiver(mRefreshHistoryReceiver,
				new IntentFilter(MokaReceiver.INTENT_FILTER_JADE_SERVER_RECEIVER));
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getSherlockActivity()).unregisterReceiver(mRefreshHistoryReceiver);
	}

	/**
	 * get the current history from the rest server
	 */
	public void refreshHistory() {
		mMokaRestService.historyEntries(this);
	}

	@Override
	public void onRefreshRequest() {
		refreshHistory();
	}

	@Override
	public void success(List<HistoryEntry> historyEntries, Response response) {
		Log.d(TAG, "success");
		mAdapter.updateHistoryItems(historyEntries);
	}

	@Override
	public void failure(RetrofitError retrofitError) {
		Log.d(TAG, "REST call failure === " + retrofitError.toString());
		Crouton.makeText(getSherlockActivity(), getResources().getString(R.string.network_error), Style.ALERT).show();
	}
}
