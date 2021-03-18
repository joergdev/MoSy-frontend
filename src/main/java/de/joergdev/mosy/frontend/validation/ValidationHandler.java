package de.joergdev.mosy.frontend.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import de.joergdev.mosy.frontend.Message;
import de.joergdev.mosy.frontend.MessageLevel;

public class ValidationHandler
{
  private final List<AbstractValidation<?>> validators = new ArrayList<>();
  private final List<Message> messages = new ArrayList<>();

  public <T> ValidationHandler addValidation(AbstractValidation<T> validation)
  {
    validators.add(validation);

    return this;
  }

  public boolean validate()
  {
    for (AbstractValidation<?> validator : validators)
    {
      validate(validator);
    }

    return !messages.stream()
        .anyMatch(m -> Arrays.asList(MessageLevel.ERROR, MessageLevel.FATAL).contains(m.getLevel()));
  }

  private void validate(AbstractValidation<?> validator)
  {
    if (!validator.validate())
    {
      messages.add(validator.getMessage());
    }
    // validation ok => check sub-validations
    else
    {
      for (AbstractValidation<?> subValidation : validator.getSubvalidations())
      {
        validate(subValidation);
      }
    }
  }

  public List<Message> getMessages()
  {
    return messages;
  }
}