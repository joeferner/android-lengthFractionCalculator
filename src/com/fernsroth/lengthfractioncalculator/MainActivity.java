package com.fernsroth.lengthfractioncalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
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

    exactInches.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        promptForValue("Inches", new OnPromptForValueOk() {

          public void ok(String val) {
            ruler.setInches(parseInches(val));
          }

          private float parseInches(String val) {
            return Float.parseFloat(val);
          }

        });
      }
    });

    exactMetric.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        promptForValue("Millimeters", new OnPromptForValueOk() {

          public void ok(String val) {
            ruler.setMillimeters(parseMillimeters(val));
          }

          private float parseMillimeters(String val) {
            return Float.parseFloat(val);
          }

        });
      }
    });

    fractionLeft.setRoundingDirection(Fraction.RoundingDirection.Down);
    fractionRight.setRoundingDirection(Fraction.RoundingDirection.Up);

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

  private interface OnPromptForValueOk {

    void ok(String val);

  }

  protected void promptForValue(String title, final OnPromptForValueOk onOk) {
    final EditText editValView = new EditText(this);
    editValView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

    Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle(title);
    alertDialog.setView(editValView);
    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        String val = editValView.getText().toString();
        onOk.ok(val);
      }
    });
    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });
    alertDialog.show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }
}
