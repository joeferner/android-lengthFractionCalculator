package com.fernsroth.lengthfractioncalculator;

import android.util.FloatMath;

public class Fraction {
  public enum RoundingDirection {
    Down, Up
  }

  private static final int DEFAULT_MAX_DENOMINATOR = 128;

  private float value;
  private int wholeNumber;
  private int numerator;
  private int denominator;
  private RoundingDirection roundingDirection;
  private int maxDenominator = DEFAULT_MAX_DENOMINATOR;

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
    this.denominator = this.maxDenominator;
    float floatNumerator = rest * this.denominator;
    if (roundingDirection == RoundingDirection.Up) {
      this.numerator = (int) FloatMath.ceil(floatNumerator);
    } else {
      this.numerator = (int) FloatMath.floor(floatNumerator);
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

    if (this.numerator == this.denominator) {
      this.numerator = 0;
      this.wholeNumber++;
    }
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
    invalidate();
  }

  public void setMaximumDenominator(int maxDenominator) {
    this.maxDenominator = maxDenominator;
    invalidate();
  }

  private void invalidate() {
    setValue(getValue());
  }

  public int getMaximumDenominator() {
    return this.maxDenominator;
  }
}
