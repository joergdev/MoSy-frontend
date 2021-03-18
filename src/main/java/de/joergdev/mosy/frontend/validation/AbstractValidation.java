package de.joergdev.mosy.frontend.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import de.joergdev.mosy.frontend.Message;
import de.joergdev.mosy.frontend.MessageLevel;

public abstract class AbstractValidation<T>
{
  private Message message;
  protected T value;
  protected Supplier<T> valueSupplier;
  private final List<Supplier<Boolean>> conditions = new ArrayList<>();

  private final List<AbstractValidation<?>> subvalidations = new ArrayList<>();

  public AbstractValidation(String msg, T value, String... msgDetails)
  {
    this(msg, MessageLevel.ERROR, value, msgDetails);
  }

  public AbstractValidation(String msg, MessageLevel level, T value, String... msgDetails)
  {
    this(msg, level, value, null, msgDetails);
  }

  public AbstractValidation(String msg, MessageLevel level, Supplier<T> valueSupplier, String... msgDetails)
  {
    this(msg, level, null, valueSupplier, msgDetails);
  }

  public AbstractValidation(String msg, MessageLevel level, T value, Supplier<T> valueSupplier,
                            String... msgDetails)
  {
    message = new Message();
    message.setLevel(level);
    message.setMsg(msg);
    message.setMessageDetails(msgDetails);

    this.setValue(value);
    this.setValueSupplier(valueSupplier);
  }

  public T getValue()
  {
    return value;
  }

  public void setValue(T value)
  {
    this.value = value;
  }

  public Message getMessage()
  {
    return message;
  }

  public void setMessage(Message message)
  {
    this.message = message;
  }

  public List<Supplier<Boolean>> getConditions()
  {
    return conditions;
  }

  public Supplier<T> getValueSupplier()
  {
    return valueSupplier;
  }

  public void setValueSupplier(Supplier<T> valueSupplier)
  {
    this.valueSupplier = valueSupplier;
  }

  public T getValueToCheck()
  {
    if (valueSupplier != null)
    {
      return valueSupplier.get();
    }
    else
    {
      return value;
    }
  }

  public List<AbstractValidation<?>> getSubvalidations()
  {
    return subvalidations;
  }

  public AbstractValidation<T> addSubValidation(AbstractValidation<?> subvalidation)
  {
    subvalidations.add(subvalidation);

    return this;
  }

  @SuppressWarnings("unchecked")
  public <K extends AbstractValidation<T>> K addCondition(Supplier<Boolean> condition)
  {
    conditions.add(condition);

    return (K) this;
  }

  public final boolean validate()
  {
    // conditions on when to execute the validation
    for (Supplier<Boolean> condition : conditions)
    {
      // if condition not passed then no validation = success validation
      if (!Boolean.TRUE.equals(condition.get()))
      {
        return true;
      }
    }

    return _validate();
  }

  protected abstract boolean _validate();
}