package com.github.joergdev.mosy.frontend.validation;

import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.shared.Utils;

public class StringNotEmpty extends AbstractValidation<String>
{
  public StringNotEmpty(String value, String fieldLabel)
  {
    this(MessageLevel.ERROR, value, fieldLabel);
  }

  public StringNotEmpty(MessageLevel level, String value, String fieldLabel)
  {
    super("field_not_empty", level, value, Resources.getLabel(fieldLabel));
  }

  @Override
  protected boolean _validate()
  {
    return !Utils.isEmpty(getValueToCheck());
  }
}