package com.github.joergdev.mosy.frontend.validation;

import java.util.Collection;
import com.github.joergdev.mosy.frontend.MessageLevel;

public class SelectionValidation extends AbstractValidation<Collection<?>>
{
  private static final String MSG_NO_SELECTION = "no_var_selected";

  public SelectionValidation(Collection<?> value, String msgDetailVar)
  {
    this(MessageLevel.ERROR, value, msgDetailVar);
  }

  public SelectionValidation(MessageLevel level, Collection<?> value, String msgDetailVar)
  {
    super(null, level, value, msgDetailVar);
  }

  @Override
  protected boolean _validate()
  {
    if (getValueToCheck().isEmpty())
    {
      getMessage().setMsg(MSG_NO_SELECTION);

      return false;
    }

    return true;
  }
}