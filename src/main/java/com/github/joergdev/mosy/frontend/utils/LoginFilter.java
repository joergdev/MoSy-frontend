package com.github.joergdev.mosy.frontend.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.security.TokenHolder;
import com.github.joergdev.mosy.frontend.view.LoginV;
import com.github.joergdev.mosy.shared.Utils;

@WebFilter(urlPatterns = {"*.xhtml"})
public class LoginFilter implements Filter
{
  @Override
  public void init(FilterConfig filterConfig)
    throws ServletException
  {
    // nothing to do here
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException,
    ServletException
  {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    HttpSession httpSesion = httpRequest.getSession();

    String url = httpRequest.getRequestURI();
    String token = httpRequest.getParameter(HttpHeaders.AUTHORIZATION);

    if (url.startsWith("/javax.faces.resource/"))
    {
      chain.doFilter(httpRequest, httpResponse);

      return;
    }

    if (!url.contains(Resources.SITE_LOGIN) && !url.contains(Resources.SITE_GOODBUY) && Utils.isEmpty(token)
        && Utils.isEmpty(TokenHolder.getToken(httpSesion)))
    {
      httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + Resources.SITE_LOGIN + "?"
                                + LoginV.VIEW_PARAM_NO_AUTH + "=true");
    }
    else
    {
      if (!Utils.isEmpty(token))
      {
        TokenHolder.setToken(httpSesion, token);
      }

      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy()
  {
    //nothing to do here
  }
}