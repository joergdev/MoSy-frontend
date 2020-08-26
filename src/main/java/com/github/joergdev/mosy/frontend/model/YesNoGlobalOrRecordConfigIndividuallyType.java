package com.github.joergdev.mosy.frontend.model;

import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.shared.Utils;

public enum YesNoGlobalOrRecordConfigIndividuallyType
{
  YES(true, "yes_global"), NO(false, "no_global"), INDIVIDUALLY(null, "individually_by_recordconfig");

  public final Boolean value;
  public final String text;

  private YesNoGlobalOrRecordConfigIndividuallyType(Boolean value, String text)
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

  public static YesNoGlobalOrRecordConfigIndividuallyType fromBoolean(Boolean b)
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

  public static Boolean toBoolean(YesNoGlobalOrRecordConfigIndividuallyType t)
  {
    return t == null
        ? null
        : t.value;
  }
}