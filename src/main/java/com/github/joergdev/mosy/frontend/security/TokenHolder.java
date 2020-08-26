package com.github.joergdev.mosy.frontend.security;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;

public class TokenHolder
{
  public static void setToken(String token)
  {
    setToken(JsfUtils.getHttpSession(true), token);
  }

  public static String getToken()
  {
    return getToken(JsfUtils.getHttpSession(false));
  }

  public static void setToken(HttpSession httpSession, String token)
  {
    if (httpSession != null)
    {
      httpSession.setAttribute(HttpHeaders.AUTHORIZATION, token);
    }
  }

  public static String getToken(HttpSession httpSession)
  {
    return httpSession == null
        ? null
        : (String) httpSession.getAttribute(HttpHeaders.AUTHORIZATION);
  }
}