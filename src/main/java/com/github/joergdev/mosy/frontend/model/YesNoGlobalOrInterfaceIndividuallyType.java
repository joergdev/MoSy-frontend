package com.github.joergdev.mosy.frontend.model;

import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.shared.Utils;

public enum YesNoGlobalOrInterfaceIndividuallyType
{
  YES(true, "yes_global"), NO(false, "no_global"), INDIVIDUALLY(null, "individually_by_interface");

  public final Boolean value;
  public final String text;

  private YesNoGlobalOrInterfaceIndividuallyType(Boolean value, String text)
  {
    this.value = value;
    this.text = text;
  }

  public String toString()
  {
    return Utils.isEmpty(text)
        ? null
        : Resources.getLabel(text);
  }

  public static YesNoGlobalOrInterfaceIndividuallyType fromBoolean(Boolean b)
  {
    if (b == null)
    {
      return INDIVIDUALLY;
    }
    else if (b)
    {
      return YES;
    }
    else
    {
      return NO;
    }
  }

  public static Boolean toBoolean(YesNoGlobalOrInterfaceIndividuallyType t)
  {
    return t == null
        ? null
        : t.value;
  }
}