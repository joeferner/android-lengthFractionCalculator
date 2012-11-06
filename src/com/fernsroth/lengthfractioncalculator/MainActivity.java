package com.fernsroth.lengthfractioncalculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {

	private RulerView ruler;
	private TextView exactInches;
	private TextView exactMetric;
	private FractionView fractionLeft;
	private FractionView fractionRight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		ruler = (RulerView) findViewById(R.id.ruler);
		exactInches = (TextView) findViewById(R.id.exactInches);
		exactMetric = (TextView) findViewById(R.id.exactMetric);
		fractionLeft = (FractionView) findViewById(R.id.fractionLeft);
		fractionRight = (FractionView) findViewById(R.id.fractionRight);

		ruler.addOnRulerScrollListener(new OnRulerScrollListener() {

			public void onRulerScroll() {
				runOnUiThread(new Runnable() {

					public void run() {
						float in = ruler.getInches();
						exactInches.setText(String.format("%.3f\"", in));
						exactMetric.setText(String.format("%.3fmm", ruler.getMillimeters()));
						fractionLeft.setValue(in);
						fractionRight.setValue(in);
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
