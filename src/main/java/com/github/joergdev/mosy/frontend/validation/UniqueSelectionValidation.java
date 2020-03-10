package com.github.joergdev.mosy.frontend.validation;

import java.util.List;
import com.github.joergdev.mosy.frontend.MessageLevel;

public class UniqueSelectionValidation extends AbstractValidation<List<?>>
{
  private static final String MSG_NO_SELECTION = "no_var_selected";
  private static final String MSG_MULTIPLE_SELECTION = "more_than_one_var_selected";

  public UniqueSelectionValidation(List<?> value, String msgDetailVar)
  {
    this(MessageLevel.ERROR, value, msgDetailVar);
  }

  public UniqueSelectionValidation(MessageLevel level, List<?> value, String msgDetailVar)
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
    else if (getValueToCheck().size() > 1)
    {
      getMessage().setMsg(MSG_MULTIPLE_SELECTION);

      return false;
    }

    return true;
  }
}