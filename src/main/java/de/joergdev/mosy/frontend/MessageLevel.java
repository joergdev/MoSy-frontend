package de.joergdev.mosy.frontend;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import de.joergdev.mosy.api.response.ResponseMessageLevel;

public enum MessageLevel
{
  INFO(0), WARN(1), ERROR(2), FATAL(3);

  public final int rank;

  private MessageLevel(int rank)
  {
    this.rank = rank;
  }

  public String getLabel()
  {
    if (INFO.equals(this))
    {
      return "info";
    }
    else if (WARN.equals(this))
    {
      return "warn";
    }
    else if (ERROR.equals(this))
    {
      return "error";
    }
    else if (FATAL.equals(this))
    {
      return "fatal";
    }

    return null;
  }

  public Severity getFacesSeverity()
  {
    if (INFO.equals(this))
    {
      return FacesMessage.SEVERITY_INFO;
    }
    else if (WARN.equals(this))
    {
      return FacesMessage.SEVERITY_WARN;
    }
    else if (ERROR.equals(this))
    {
      return FacesMessage.SEVERITY_ERROR;
    }
    else if (FATAL.equals(this))
    {
      return FacesMessage.SEVERITY_FATAL;
    }

    return null;
  }

  public static MessageLevel getByResponseMessageLevel(ResponseMessageLevel rspmlvl)
  {
    if (ResponseMessageLevel.DEBUG.equals(rspmlvl) || ResponseMessageLevel.INFO.equals(rspmlvl))
    {
      return MessageLevel.INFO;
    }
    else if (ResponseMessageLevel.WARN.equals(rspmlvl))
    {
      return MessageLevel.WARN;
    }
    else if (ResponseMessageLevel.ERROR.equals(rspmlvl))
    {
      return MessageLevel.ERROR;
    }
    else if (ResponseMessageLevel.FATAL.equals(rspmlvl))
    {
      return MessageLevel.FATAL;
    }

    return null;
  }
}