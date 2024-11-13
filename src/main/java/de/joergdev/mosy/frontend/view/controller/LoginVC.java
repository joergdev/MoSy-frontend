package de.joergdev.mosy.frontend.view.controller;

import de.joergdev.mosy.api.model.Tenant;
import de.joergdev.mosy.api.response.system.LoginResponse;
import de.joergdev.mosy.api.response.tenant.LoadAllResponse;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.security.TokenHolder;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.validation.NotNull;
import de.joergdev.mosy.frontend.validation.StringNotEmpty;
import de.joergdev.mosy.frontend.view.LoginV;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;

public class LoginVC extends AbstractViewController<LoginV>
{
  public void init()
  {
    new InitExecution().execute();
  }

  private class InitExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // none
    }

    @Override
    protected void _execute()
      throws Exception
    {
      showMessageOnNoAuth();

      loadAndTransferTanencyData();
    }

    private void loadAndTransferTanencyData()
    {
      LoadAllResponse loadTenantsResponse = invokeApiCall(apiClient -> apiClient.loadTenants());

      view.setMultiTanencyEnabled(loadTenantsResponse.isMultiTanencyEnabled());

      if (loadTenantsResponse.isMultiTanencyEnabled())
      {
        view.getTenants().addAll(loadTenantsResponse.getTenants());
      }
      else
      {
        view.getTenants().clear();
        view.setTenantSelected(null);
      }
    }

    private void showMessageOnNoAuth()
    {
      String paramNoAuth = JsfUtils.getViewParameter(LoginV.VIEW_PARAM_NO_AUTH);

      if (Boolean.TRUE.equals(Boolean.valueOf(paramNoAuth)))
      {
        addMessage(MessageLevel.ERROR, "please_login");
      }
    }
  }

  public void doLogin()
  {
    new LoginExecution().execute();
  }

  private class LoginExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      if (view.isMultiTanencyEnabled())
      {
        addValidation(new NotNull(view.getTenantSelected(), "tenant"));
      }

      addValidation(new StringNotEmpty(view.getPassword(), "password"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Tenant tenant = view.getTenantSelected();
      Integer hash = view.getPassword().hashCode();

      Integer tenantId = getTenantIdForLogin(tenant);

      LoginResponse response = invokeApiCall(apiClient -> apiClient.systemLogin(tenantId, hash));

      TokenHolder.setTenant(tenant);
      TokenHolder.setToken(response.getToken());

      JsfUtils.redirect(Resources.SITE_MAIN);
    }

    private Integer getTenantIdForLogin(Tenant tenant)
    {
      Integer tenantId = null;
      if (tenant != null)
      {
        tenantId = view.getTenants().stream() //
            .filter(t -> t.equals(tenant)).findAny() //
            .orElseThrow(() -> new IllegalStateException()) //
            .getTenantId();

        tenant.setTenantId(tenantId);
      }

      return tenantId;
    }
  }

  public void updateComponents()
  {
    view.setTenantEditDeletePossible(view.getTenantSelected() != null);
  }

  public void doCreateTenant()
  {
    new CreateTenantExecution().execute();
  }

  private class CreateTenantExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validation
    }

    @Override
    protected void _execute()
      throws Exception
    {
      JsfUtils.redirect(Resources.SITE_TENANT);
    }
  }
}
