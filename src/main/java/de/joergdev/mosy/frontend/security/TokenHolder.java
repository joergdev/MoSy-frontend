package de.joergdev.mosy.frontend.security;

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.HttpHeaders;
import de.joergdev.mosy.api.model.Tenant;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.shared.Utils;

public class TokenHolder
{
  private static final String ATTR_TENANT_ID = "MOSY_TENANT_ID";
  private static final String ATTR_TENANT_NAME = "MOSY_TENANT_NAME";

  public static void setToken(String token)
  {
    setToken(JsfUtils.getHttpSession(true), token);
  }
  
  public static void setToken(HttpSession httpSession,String token)
  {
    setAttribute(httpSession, HttpHeaders.AUTHORIZATION, token);
  }

  public static String getToken()
  {
    return getToken(JsfUtils.getHttpSession(false));
  }

  public static String getToken(HttpSession httpSession)
  {
    return getAttribute(httpSession, HttpHeaders.AUTHORIZATION);
  }

  public static void setTenant(Tenant tenant)
  {
    tenant = Utils.nvl(tenant, new Tenant());

    HttpSession httpSession = JsfUtils.getHttpSession(true);

    setAttribute(httpSession, ATTR_TENANT_ID, tenant.getTenantId());
    setAttribute(httpSession, ATTR_TENANT_NAME, tenant.getName());
  }

  public static Tenant getTenant()
  {
    HttpSession httpSession = JsfUtils.getHttpSession(true);

    Integer tenantId = getAttribute(httpSession, ATTR_TENANT_ID);
    if (tenantId != null)
    {
      Tenant tenant = new Tenant();
      tenant.setTenantId(tenantId);
      tenant.setName(getAttribute(httpSession, ATTR_TENANT_NAME));

      return tenant;
    }

    return null;
  }

  public static void setAttribute(HttpSession httpSession, String attribute, Object value)
  {
    if (httpSession != null)
    {
      httpSession.setAttribute(attribute, value);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getAttribute(HttpSession httpSession, String attribute)
  {
    return httpSession == null ? null : (T) httpSession.getAttribute(attribute);
  }
}
