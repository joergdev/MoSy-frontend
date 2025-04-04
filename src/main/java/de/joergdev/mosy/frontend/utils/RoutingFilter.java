package de.joergdev.mosy.frontend.utils;

import java.io.IOException;
import java.util.Arrays;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.shared.Utils;

@WebFilter(urlPatterns = {"*.xhtml", "*.html"})
public class RoutingFilter implements Filter
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

    final String url = httpRequest.getRequestURI();

    if (url.startsWith("/jakarta.faces.resource/"))
    {
      chain.doFilter(httpRequest, httpResponse);

      return;
    }

    String redirectURL = null;

    // Route html to xhtml
    if (url.endsWith(".html"))
    {
      redirectURL = url.replaceAll(".html", ".xhtml");
    }

    // Route subsites to mainsite
    redirectURL = getRedirectUrlIfSubsite(httpRequest, url, redirectURL);

    if (redirectURL != null)
    {
      httpResponse.sendRedirect(redirectURL);
    }
    else
    {
      chain.doFilter(httpRequest, httpResponse);
    }
  }

  private String getRedirectUrlIfSubsite(HttpServletRequest httpRequest, final String url,
                                         final String redirectURLSet)
  {
    String redirectURL = null;

    if (Arrays
        .asList(Resources.SITE_MAIN_BASEDATA, Resources.SITE_MAIN_INTERFACES,
            Resources.SITE_MAIN_MOCK_PROFILES, Resources.SITE_MAIN_RECORDSESSIONS,
            Resources.SITE_MAIN_RECORDS, Resources.SITE_MAIN_RECORD)
        .stream().anyMatch(site -> Utils.nvl(redirectURLSet, url).contains(site)))
    {
      redirectURL = httpRequest.getContextPath() + "/" + Resources.SITE_MAIN;
    }
    else if (Arrays
        .asList(Resources.SITE_INTERFACE_BASEDATA, Resources.SITE_INTERFACE_METHODS,
            Resources.SITE_INTERFACE_METHOD_BASEDATA, Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW,
            Resources.SITE_INTERFACE_METHOD_MOCKDATA, Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW,
            Resources.SITE_INTERFACE_METHOD_RECORDCONF)
        .stream().anyMatch(site -> Utils.nvl(redirectURLSet, url).contains(site)))
    {
      redirectURL = httpRequest.getContextPath() + "/" + Resources.SITE_INTERFACE;
    }

    return Utils.nvl(redirectURL, redirectURLSet);
  }

  @Override
  public void destroy()
  {
    //nothing to do here
  }
}