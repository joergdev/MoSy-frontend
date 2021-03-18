package de.joergdev.mosy.frontend.validation;

import java.math.BigDecimal;
import java.util.function.Supplier;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;

public class NumberValidator extends AbstractValidation<Object>
{
  private final boolean orNull;
  private final Number min;
  private final Number max;

  public NumberValidator(Object value, String fieldLabel, boolean orNull, Number min, Number max)
  {
    this(MessageLevel.ERROR, value, fieldLabel, orNull, min, max);
  }

  public NumberValidator(Supplier<Object> valueSupplier, String fieldLabel, boolean orNull, Number min,
                         Number max)
  {
    this(MessageLevel.ERROR, valueSupplier, fieldLabel, orNull, min, max);
  }

  public NumberValidator(MessageLevel level, Object value, String fieldLabel, boolean orNull, Number min,
                         Number max)
  {
    this(level, value, null, fieldLabel, orNull, min, max);
  }

  public NumberValidator(MessageLevel level, Supplier<Object> valueSupplier, String fieldLabel,
                         boolean orNull, Number min, Number max)
  {
    this(level, null, valueSupplier, fieldLabel, orNull, min, max);
  }

  private NumberValidator(MessageLevel level, Object value, Supplier<Object> valueSupplier, String fieldLabel,
                          boolean orNull, Number min, Number max)
  {
    super("var_not_a_valid_number", level, value, valueSupplier,
        new String[] {Resources.getLabel(fieldLabel)});

    this.orNull = orNull;
    this.min = min;
    this.max = max;
  }

  @Override
  protected boolean _validate()
  {
    Object value = getValueToCheck();

    if (value == null && orNull)
    {
      return true;
    }

    Number number = null;

    if (value instanceof Number)
    {
      number = (Number) value;
    }
    else if (value instanceof String)
    {
      String str = (String) value;

      try
      {
        number = Integer.valueOf(str);
      }
      catch (Exception ex)
      {
        try
        {
          number = new BigDecimal(str);
        }
        catch (Exception ex2)
        {
          return false;
        }
      }
    }
    else
    {
      return false;
    }

    if (min != null && number.doubleValue() < min.doubleValue())
    {
      return false;
    }

    if (max != null && number.doubleValue() > max.doubleValue())
    {
      return false;
    }

    return true;
  }
}