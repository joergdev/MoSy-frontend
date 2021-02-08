package com.github.joergdev.mosy.frontend.validation;

import java.util.function.Supplier;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;

public class NotFalse extends AbstractValidation<Boolean>
{
  public NotFalse(Boolean value, String fieldLabel)
  {
    this(MessageLevel.ERROR, value, fieldLabel);
  }

  public NotFalse(Supplier<Boolean> valueSupplier, String fieldLabel)
  {
    this(MessageLevel.ERROR, valueSupplier, fieldLabel);
  }

  public NotFalse(MessageLevel level, Boolean value, String fieldLabel)
  {
    this(level, value, null, fieldLabel);
  }

  public NotFalse(MessageLevel level, Supplier<Boolean> valueSupplier, String fieldLabel)
  {
    this(level, null, valueSupplier, fieldLabel);
  }

  private NotFalse(MessageLevel level, Boolean value, Supplier<Boolean> valueSupplier, String fieldLabel)
  {
    super("field_not_false", level, value, valueSupplier, new String[] {Resources.getLabel(fieldLabel)});
  }

  @Override
  protected boolean _validate()
  {
    return !Boolean.FALSE.equals(getValueToCheck());
  }
}