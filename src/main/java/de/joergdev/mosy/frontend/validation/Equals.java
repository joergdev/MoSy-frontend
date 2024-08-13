package de.joergdev.mosy.frontend.validation;

import java.util.Objects;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.validation.model.CompareObjects;

public class Equals extends AbstractValidation<CompareObjects>
{
  public Equals(CompareObjects value, String fieldLabelA, String fieldLabelB)
  {
    this(MessageLevel.ERROR, value, fieldLabelA, fieldLabelB);
  }

  public Equals(MessageLevel level, CompareObjects value, String fieldLabelA, String fieldLabelB)
  {
    super("values_not_machting", level, Objects.requireNonNull(value, "CompareObjects"), Resources.getLabel(fieldLabelA), Resources.getLabel(fieldLabelB));
  }

  @Override
  protected boolean _validate()
  {
    CompareObjects compareObjects = getValueToCheck();

    return Objects.equals(compareObjects.getA(), compareObjects.getB());
  }
}
