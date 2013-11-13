package com.fernsroth.lengthfractioncalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

  private RulerView ruler;
  private TextView exactInches;
  private TextView exactMetric;
  private FractionView fractionLeft;
  private FractionView fractionRight;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);

    ruler = (RulerView) findViewById(R.id.ruler);
    exactInches = (TextView) findViewById(R.id.exactInches);
    exactMetric = (TextView) findViewById(R.id.exactMetric);
    fractionLeft = (FractionView) findViewById(R.id.fractionLeft);
    fractionRight = (FractionView) findViewById(R.id.fractionRight);

    exactInches.setOnClickListener(exactInchesOnClick);
    exactMetric.setOnClickListener(exactMetricOnClick);

    fractionLeft.setRoundingDirection(Fraction.RoundingDirection.Down);
    fractionRight.setRoundingDirection(Fraction.RoundingDirection.Up);

    fractionLeft.setOnClickListener(fractionClickListener);
    fractionRight.setOnClickListener(fractionClickListener);

    ruler.addOnRulerScrollListener(rulerOnScroll);

    loadState(state);
  }

  OnRulerScrollListener rulerOnScroll = new OnRulerScrollListener() {

    public void onRulerScroll() {
      runOnUiThread(new Runnable() {

        public void run() {
          float in = ruler.getInches();
          switch (ruler.getInchesFormat()) {
            case Inches:
              exactInches.setText(String.format("%.3f\"", in));
              break;
            case FeetAndInches:
              int feet = (int) Math.floor(in / 12.0);
              exactInches.setText(String.format("%d' %.21f\"", feet, in - (12 * feet)));
              break;
          }
          if (ruler.getMillimeters() > 1000) {
            exactMetric.setText(String.format("%.3fm", ruler.getMillimeters() / 1000.0));
          } else {
            exactMetric.setText(String.format("%.3fmm", ruler.getMillimeters()));
          }
          fractionLeft.setValue(in);
          fractionRight.setValue(in);
        }
      });
    }
  };

  OnClickListener fractionClickListener = new OnClickListener() {

    public void onClick(View v) {
      final Spinner valueSpinnerView = new Spinner(MainActivity.this);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.maximum_denominator, android.R.layout.simple_spinner_item);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      valueSpinnerView.setAdapter(adapter);
      for (int i = 0; i < valueSpinnerView.getCount(); i++) {
        if (Integer.parseInt((String) valueSpinnerView.getItemAtPosition(i)) == fractionLeft.getMaximumDenominator()) {
          valueSpinnerView.setSelection(i);
          break;
        }
      }

      Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
      alertDialog.setTitle("Maximum Denominator");
      alertDialog.setView(valueSpinnerView);
      alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          String valStr = (String) valueSpinnerView.getSelectedItem();
          int val = Integer.parseInt(valStr);
          setMaximumDenominator(val);
        }
      });
      alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        }
      });
      alertDialog.show();
    }
  };

  OnClickListener exactMetricOnClick = new OnClickListener() {

    public void onClick(View v) {
      promptForValue("Millimeters", ruler.getMillimeters(), new OnPromptForValueOk() {

        public void ok(String val) {
          ruler.setMillimeters(parseMillimeters(val));
        }

        private float parseMillimeters(String val) {
          try {
            return Float.parseFloat(val);
          } catch (Exception ex) {
            AlertDialog alertDialog;
            alertDialog = new Builder(MainActivity.this).create();
            alertDialog.setTitle("Parse Error");
            alertDialog.setMessage("Could not parse millimeters.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
              }
            });
            alertDialog.show();
            return 0.0f;
          }
        }

      });
    }
  };

  OnClickListener exactInchesOnClick = new OnClickListener() {

    public void onClick(View v) {
      LayoutInflater inflater = getLayoutInflater();
      View dialogContent = inflater.inflate(R.layout.dialog_inches, null);

      final EditText editValView = (EditText) dialogContent.findViewById(R.id.value);
      editValView.setText(Float.toString(ruler.getInches()));
      editValView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

      final Spinner inchesFormatView = (Spinner) dialogContent.findViewById(R.id.format);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.inches_format, android.R.layout.simple_spinner_item);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      inchesFormatView.setAdapter(adapter);
      switch (ruler.getInchesFormat()) {
        case FeetAndInches:
          inchesFormatView.setSelection(1);
          break;
        default:
        case Inches:
          inchesFormatView.setSelection(0);
          break;
      }

      Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
      alertDialog.setTitle("Inches Setup");
      alertDialog.setView(dialogContent);
      alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          String val = editValView.getText().toString();
          ruler.setInches(parseInches(val));
          ruler.setInchesFormat(InchesFormat.parse((String) inchesFormatView.getSelectedItem()));
        }

        private float parseInches(String val) {
          try {
            return Float.parseFloat(val);
          } catch (Exception ex) {
            AlertDialog alertDialog;
            alertDialog = new Builder(MainActivity.this).create();
            alertDialog.setTitle("Parse Error");
            alertDialog.setMessage("Could not parse inches.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
              }
            });
            alertDialog.show();
            return 0.0f;
          }
        }
      });
      alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        }
      });
      alertDialog.show();
    }
  };

  private interface OnPromptForValueOk {

    void ok(String val);

  }

  protected void promptForValue(String title, float val, final OnPromptForValueOk onOk) {
    final EditText editValView = new EditText(this);
    editValView.setText(Float.toString(val));
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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_about:
        showAbout();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showAbout() {
    Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle("About");
    alertDialog.setMessage("Length Fraction Calculator\nBy: Joe Ferner");
    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {

      }
    });
    alertDialog.show();
  }

  protected void setMaximumDenominator(int val) {
    fractionLeft.setMaximumDenominator(val);
    fractionRight.setMaximumDenominator(val);
  }

  @Override
  protected void onRestoreInstanceState(Bundle state) {
    super.onRestoreInstanceState(state);
    loadState(state);
  }

  private void loadState(Bundle state) {
    if (state == null) {
      return;
    }
    this.ruler.setInches(state.getFloat("inches", 0.0f));
    setMaximumDenominator(state.getInt("maximumDenominator", 128));
  }

  @Override
  protected void onSaveInstanceState(Bundle state) {
    state.putFloat("inches", this.ruler.getInches());
    state.putInt("maximumDenominator", this.fractionLeft.getMaximumDenominator());
    super.onSaveInstanceState(state);
  }
}
