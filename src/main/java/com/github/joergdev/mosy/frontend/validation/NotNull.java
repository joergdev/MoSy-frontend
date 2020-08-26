package com.github.joergdev.mosy.frontend.validation;

import java.util.function.Supplier;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;

public class NotNull extends AbstractValidation<Object>
{
  public NotNull(Object value, String fieldLabel)
  {
    this(MessageLevel.ERROR, value, fieldLabel);
  }

  public NotNull(Supplier<Object> valueSupplier, String fieldLabel)
  {
    this(MessageLevel.ERROR, valueSupplier, fieldLabel);
  }

  public NotNull(MessageLevel level, Object value, String fieldLabel)
  {
    this(level, value, null, fieldLabel);
  }

  public NotNull(MessageLevel level, Supplier<Object> valueSupplier, String fieldLabel)
  {
    this(level, null, valueSupplier, fieldLabel);
  }

  private NotNull(MessageLevel level, Object value, Supplier<Object> valueSupplier, String fieldLabel)
  {
    super("field_not_null", level, value, valueSupplier, new String[] {Resources.getLabel(fieldLabel)});
  }

  @Override
  protected boolean _validate()
  {
    return getValueToCheck() != null;
  }
}