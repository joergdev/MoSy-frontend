package de.joergdev.mosy.frontend.validation;

import java.util.Collection;
import java.util.function.Function;

public class UniqueData<K> extends AbstractValidation<Object>
{
  private Collection<K> col;
  private Function<K, Boolean> check;

  public UniqueData(Collection<K> col, Function<K, Boolean> check)
  {
    super("data_already_exists", null);

    this.col = col;
    this.check = check;
  }

  @Override
  protected boolean _validate()
  {
    if (col != null)
    {
      for (K k : col)
      {
        // Data already exists
        if (check.apply(k))
        {
          return false;
        }
      }
    }

    return true;
  }
}
