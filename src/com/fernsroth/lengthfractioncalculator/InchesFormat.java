package com.fernsroth.lengthfractioncalculator;

public enum InchesFormat {
  Inches,
  FeetAndInches;

  public static InchesFormat parse(String str) {
    str = str.toLowerCase();
    if ("inches".equals(str)) {
      return Inches;
    }
    if ("feet and inches".equals(str)) {
      return FeetAndInches;
    }
    return Inches;
  }
}
