package de.joergdev.mosy.frontend;

import java.util.ResourceBundle;

public class Resources
{
  public static final String PROPERTY_UPLOAD_MOCKDATA_SINGLEMODE = "upload_mockdata_singlemode";

  public static final String SITE_LOGIN = "login.xhtml";
  public static final String SITE_GOODBUY = "goodbye.xhtml";

  public static final String SITE_MAIN = "main.xhtml";
  public static final String SITE_MAIN_BASEDATA = "main_basedata.xhtml";
  public static final String SITE_MAIN_INTERFACES = "main_interfaces.xhtml";
  public static final String SITE_MAIN_MOCK_PROFILES = "main_mockprofiles.xhtml";
  public static final String SITE_MAIN_RECORDS = "main_records.xhtml";
  public static final String SITE_MAIN_RECORD = "main_record.xhtml";
  public static final String SITE_MAIN_RECORDSESSIONS = "main_recordsessions.xhtml";

  public static final String SITE_INTERFACE = "interface.xhtml";
  public static final String SITE_INTERFACE_BASEDATA = "interface_basedata.xhtml";
  public static final String SITE_INTERFACE_METHODS = "interface_methods.xhtml";
  public static final String SITE_INTERFACE_METHOD_BASEDATA = "interface_method-basedata.xhtml";
  public static final String SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW = "interface_method-mockdata-overview.xhtml";
  public static final String SITE_INTERFACE_METHOD_MOCKDATA = "interface_method-mockdata.xhtml";
  public static final String SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW = "interface_method-recordconfig-overview.xhtml";
  public static final String SITE_INTERFACE_METHOD_RECORDCONF = "interface_method-recordconfig.xhtml";

  public static final String SITE_MOCK_PROFILE = "mockprofile.xhtml";

  public static final String SITE_RECORD_AS_MOCKDATA = "recordAsMockdata.xhtml";

  public static final String SITE_UPLOAD_MOCKDATA = "uploadMockdata.xhtml";

  private static final ResourceBundle MOSY_FRONTEND = ResourceBundle.getBundle("mosy_frontend");

  private static final ResourceBundle LABELS = ResourceBundle.getBundle("mosy_web_labels");

  private static final ResourceBundle INFO_MESSAGES = ResourceBundle.getBundle("mosy_web_info_messages");
  private static final ResourceBundle WARN_MESSAGES = ResourceBundle.getBundle("mosy_web_warn_messages");
  private static final ResourceBundle ERROR_MESSAGES = ResourceBundle.getBundle("mosy_web_error_messages");
  private static final ResourceBundle FATAL_ERROR_MESSAGES = ResourceBundle
      .getBundle("mosy_web_fatal_error_messages");

  public static String getProperty(String key)
  {
    return MOSY_FRONTEND.getString(key);
  }

  public static String getMessage(MessageLevel level, String key, String... details)
  {
    if (MessageLevel.INFO.equals(level))
    {
      return getInfoMessage(key, details);
    }
    else if (MessageLevel.WARN.equals(level))
    {
      return getWarnMessage(key, details);
    }
    else if (MessageLevel.ERROR.equals(level))
    {
      return getErrorMessage(key, details);
    }
    else if (MessageLevel.FATAL.equals(level))
    {
      return getFatalErrorMessage(key, details);
    }

    throw new IllegalArgumentException("unknown level: " + level);
  }

  public static String getInfoMessage(String key, String... details)
  {
    return getMessage(INFO_MESSAGES, key, details);
  }

  public static String getWarnMessage(String key, String... details)
  {
    return getMessage(WARN_MESSAGES, key, details);
  }

  public static String getErrorMessage(String key, String... details)
  {
    return getMessage(ERROR_MESSAGES, key, details);
  }

  public static String getFatalErrorMessage(String key, String... details)
  {
    return getMessage(FATAL_ERROR_MESSAGES, key, details);
  }

  private static String getMessage(ResourceBundle rb, String key, String... details)
  {
    String msg = rb.getString(key);

    if (details != null && details.length > 0)
    {
      msg = String.format(msg, (Object[]) details);
    }

    return msg;
  }

  public static String getLabel(String key)
  {
    return LABELS.getString(key);
  }
}