package de.joergdev.mosy.frontend.utils;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.HttpHeaders;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.security.TokenHolder;
import de.joergdev.mosy.frontend.view.LoginV;
import de.joergdev.mosy.shared.Utils;

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

    if (url.startsWith("/jakarta.faces.resource/"))
    {
      chain.doFilter(httpRequest, httpResponse);

      return;
    }

    if (!url.contains(Resources.SITE_LOGIN) && !url.contains(Resources.SITE_GOODBUY) && !url.contains(Resources.SITE_TENANT) && Utils.isEmpty(token)
        && Utils.isEmpty(TokenHolder.getToken(httpSesion)))
    {
      httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + Resources.SITE_LOGIN + "?" + LoginV.VIEW_PARAM_NO_AUTH + "=true");
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
