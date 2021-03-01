package de.joergdev.mosy.frontend.view.controller.core;

import de.joergdev.mosy.frontend.MessageLevel;

public class BusinessLevelException extends Exception
{
  private MessageLevel level;

  public BusinessLevelException(String msg)
  {
    this(MessageLevel.ERROR, msg);
  }

  public BusinessLevelException(MessageLevel level, String msg)
  {
    super(msg);

    this.level = level;
  }

  public MessageLevel getLevel()
  {
    return level;
  }

  public void setLevel(MessageLevel level)
  {
    this.level = level;
  }
}
