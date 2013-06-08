package fr.utc.nf28.moka.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import fr.utc.nf28.moka.R;
import fr.utc.nf28.moka.ui.base.MokaUpActivity;

public class TutoCollectionActivity extends MokaUpActivity {

	private TutoCollectionPagerAdapter mTutoCollectionPagerAdapter;

	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuto_activity_collection);

		mTutoCollectionPagerAdapter = new TutoCollectionPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.tuto_pager);
		mViewPager.setAdapter(mTutoCollectionPagerAdapter);
	}

	/**
	 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
	 * representing an object in the collection.
	 */
	public static class TutoCollectionPagerAdapter extends FragmentStatePagerAdapter {

		public TutoCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new TutoCollectionFragment();
			Bundle args = new Bundle();
			switch (i) {
				case 0:
					args.putInt(TutoCollectionFragment.ARG_DRAWABLE, R.drawable.tuto_connexion);
					break;
				default:
					args.putInt(TutoCollectionFragment.ARG_DRAWABLE, R.drawable.logo);
					break;
			}
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "Connexion";
				case 1:
					return "Vue principale";
				case 2:
					return "Creation";
				case 3:
					return "Items en cours";
				case 4:
					return "Edition";
				case 5:
					return "Déplacement";
				default:
					return "tuto " + (position + 1);
			}
		}
	}

}
