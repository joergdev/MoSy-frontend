package de.joergdev.mosy.frontend;

public class Message
{
  private MessageLevel level;
  private Integer code;
  private String msg;
  private String[] messageDetails;

  public Message()
  {

  }

  public Message(MessageLevel level, String msg, String... messageDetails)
  {
    this(level, null, msg, messageDetails);
  }

  public Message(MessageLevel level, Integer code, String msg, String... messageDetails)
  {
    this.level = level;
    this.code = code;
    this.msg = msg;
    this.messageDetails = messageDetails;
  }

  public String getFullMessage()
  {
    StringBuilder bui = new StringBuilder();

    if (code != null)
    {
      bui.append(code).append(" - ");
    }

    bui.append(Resources.getMessage(level, msg, messageDetails));

    return bui.toString();
  }

  public String getMsg()
  {
    return msg;
  }

  public void setMsg(String msg)
  {
    this.msg = msg;
  }

  public MessageLevel getLevel()
  {
    return level;
  }

  public void setLevel(MessageLevel level)
  {
    this.level = level;
  }

  public String[] getMessageDetails()
  {
    return messageDetails;
  }

  public void setMessageDetails(String... messageDetails)
  {
    this.messageDetails = messageDetails;
  }

  public Integer getCode()
  {
    return code;
  }

  public void setCode(Integer code)
  {
    this.code = code;
  }
}