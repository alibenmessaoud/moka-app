package fr.utc.nf28.moka.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.utc.nf28.moka.R;
import fr.utc.nf28.moka.data.ComputerItem;
import fr.utc.nf28.moka.data.ComputerType;
import fr.utc.nf28.moka.data.MediaType;
import fr.utc.nf28.moka.data.MokaItem;
import fr.utc.nf28.moka.data.MokaType;
import fr.utc.nf28.moka.data.TextType;
import fr.utc.nf28.moka.io.agent.IAndroidAgent;
import fr.utc.nf28.moka.io.receiver.CreationReceiver;
import fr.utc.nf28.moka.io.receiver.MokaReceiver;
import fr.utc.nf28.moka.ui.base.MokaUpActivity;
import fr.utc.nf28.moka.util.CroutonUtils;
import fr.utc.nf28.moka.util.JadeUtils;

import static fr.utc.nf28.moka.util.LogUtils.makeLogTag;

public class NewItemActivity extends MokaUpActivity implements CreationReceiver.OnCreationCallbackListener, EditItemFragment.Callbacks {
	public static final String ARG_TYPE = "arg_type";
	private static final String TAG = makeLogTag(NewItemActivity.class);
	private final IntentFilter mIntentFilter = new IntentFilter(MokaReceiver.INTENT_FILTER_JADE_SERVER_RECEIVER);
	/**
	 * broadcast receiver use to catch agent callback
	 */
	private CreationReceiver mJadeServerReceiver;
	/**
	 * type of the item
	 */
	private String mType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_item_activity);

		//create new receiver
		mJadeServerReceiver = new CreationReceiver(this);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.new_item_actionbar_title));

		if (savedInstanceState == null & getIntent().hasExtra(ARG_TYPE)) {

			//retrieve MokaType from intent
			final MokaType type = (MokaType) getIntent().getExtras().getParcelable(ARG_TYPE);

			//add fragment
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.new_item_container, NewItemFragment.newInstance(type))
					.commit();


			if (type instanceof ComputerType.UmlType) {
				mType = ComputerType.UmlType.KEY_TYPE;
			} else if (type instanceof TextType.PostItType) {
				mType = TextType.PostItType.KEY_TYPE;
			} else if (type instanceof MediaType.ImageType) {
				mType = MediaType.ImageType.KEY_TYPE;
			} else {
				Crouton.makeText(this, "implémenter création pour " + type.getClass().toString(),
						CroutonUtils.INFO_MOKA_STYLE).show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mJadeServerReceiver, mIntentFilter);

		//send creation request to the SMA
		if (mType != null) {
			final IAndroidAgent agent = JadeUtils.getAndroidAgentInterface();
			agent.createItem(mType);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mJadeServerReceiver);
	}

	@Override
	public void onSuccess(int id) {
		Crouton.makeText(this, "id from server :" + String.valueOf(id) + ". Ready for editing.",
				Style.CONFIRM).show();
		//TODO use the real type
		final MokaItem item = new ComputerItem.UmlItem("Uml_item" + String.valueOf(id));
		item.setId(id);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.new_item_container, EditItemFragment.newInstance(item))
				.commit();
	}

	@Override
	public void onError() {
	}

	@Override
	public void onItemDeletion(MokaItem item) {
		final IAndroidAgent agent = JadeUtils.getAndroidAgentInterface();
		agent.deleteItem(item.getId());
		setResult(EditItemActivity.RESULT_DELETE);
		finish();
	}
}
