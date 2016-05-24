package com.jahepi.tank;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AndroidLauncher extends AndroidApplication implements com.jahepi.tank.ads.AdListener {
	
	private static final String TAG = "AndroidLauncher";
	protected AdView adView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MobileAds.initialize(this, Config.ADMOB_KEY);

		RelativeLayout relativeLayout = new RelativeLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gameView = initializeForView(new TankField(this), config);

		relativeLayout.addView(gameView);

		adView = new AdView(this);
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				int visibility = adView.getVisibility();
				adView.setVisibility(View.GONE);
				adView.setVisibility(visibility);
				Gdx.app.log(TAG, "Ad loaded");
			}
		});

		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(Config.ADMOB_KEY);

		AdRequest.Builder builder = new AdRequest.Builder();
		builder.addTestDevice("91225038BBD19AC2FC79B5F07EB41AF8");

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);

		relativeLayout.addView(adView, params);
		adView.loadAd(builder.build());

		setContentView(relativeLayout);
	}

	@Override
	public void show(final boolean active) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (active) {
					adView.setVisibility(View.VISIBLE);
				} else {
					adView.setVisibility(View.GONE);
				}
			}
		});
	}
}
