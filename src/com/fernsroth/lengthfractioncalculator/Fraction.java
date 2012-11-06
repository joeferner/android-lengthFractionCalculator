package com.fernsroth.lengthfractioncalculator;

import android.util.FloatMath;

public class Fraction {
  public enum RoundingDirection {
    Down, Up
  };

  private float value;
  private int wholeNumber;
  private int numerator;
  private int denominator;
  private RoundingDirection roundingDirection;

  public Fraction() {
    setValue(0.0f);
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
    this.wholeNumber = (int) FloatMath.floor(this.value);
    float rest = this.value - FloatMath.floor(this.value);
    this.denominator = 128;
    float floatNumerator = rest * this.denominator;
    if (roundingDirection == RoundingDirection.Up) {
      this.numerator = (int) Math.ceil(floatNumerator);
    } else {
      this.numerator = (int) Math.floor(floatNumerator);
    }
    reduce();
  }

  private void reduce() {
    int i;
    for (i = this.denominator; i >= 1; i--) {
      if ((this.numerator % i == 0) && (this.denominator % i == 0)) {
        break;
      }
    }
    this.numerator = this.numerator / i;
    this.denominator = this.denominator / i;
  }

  public int getWholeNumber() {
    return this.wholeNumber;
  }

  public int getNumerator() {
    return this.numerator;
  }

  public int getDenominator() {
    return this.denominator;
  }

  public void setRoundingDirection(RoundingDirection roundingDirection) {
    this.roundingDirection = roundingDirection;
  }
}
