package de.joergdev.mosy.frontend.utils;

import java.io.IOException;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import de.joergdev.mosy.frontend.security.TokenHolder;

public class JsfUtils
{
  public static String getViewParameter(String key)
  {
    return getExternalContext().getRequestParameterMap().get(key);
  }

  public static String getPreviousPage()
  {
    return getExternalContext().getRequestHeaderMap().get("referer");
  }

  public static void showMessage(String messagesComp, Severity severity, String summary, String detail)
  {
    getFacesContext().addMessage(messagesComp, new FacesMessage(severity, summary, detail));
  }

  public static HttpSession getHttpSession(boolean createIfNull)
  {
    ExternalContext ec = getExternalContext();

    return ec == null ? null : (HttpSession) ec.getSession(createIfNull);
  }

  public static FacesContext getFacesContext()
  {
    return FacesContext.getCurrentInstance();
  }

  public static ExternalContext getExternalContext()
  {
    FacesContext fc = getFacesContext();

    return fc == null ? null : fc.getExternalContext();
  }

  public static void redirect(String target)
    throws IOException
  {
    redirect(target, null);
  }

  public static void redirect(String target, Map<String, Object> queryParams)
    throws IOException
  {
    if (queryParams != null && !queryParams.isEmpty())
    {
      StringBuilder bui = new StringBuilder(target);

      boolean firstParam = true;

      for (String paramName : queryParams.keySet())
      {
        Object paramValue = queryParams.get(paramName);
        if (paramValue != null)
        {
          bui.append(firstParam ? "?" : "&");

          bui.append(paramName).append("=").append(paramValue);

          firstParam = false;
        }
      }

      target = bui.toString();
    }

    getExternalContext().redirect(target);
  }

  public static void invalidateSession()
  {
    getExternalContext().invalidateSession();

    TokenHolder.setTenant(null);
    TokenHolder.setToken(null);
  }
}
