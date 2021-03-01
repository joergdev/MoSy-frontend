package de.joergdev.mosy.frontend.utils;

public enum WidthUnit
{
  PERCENT(""), PIXEL("px");

  public final String postfix;

  private WidthUnit(String postfix)
  {
    this.postfix = postfix;
  }
}