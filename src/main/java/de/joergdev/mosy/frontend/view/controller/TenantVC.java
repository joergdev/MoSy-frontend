package de.joergdev.mosy.frontend.view.controller;

import de.joergdev.mosy.api.model.Tenant;
import de.joergdev.mosy.frontend.Message;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.security.TokenHolder;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.validation.Equals;
import de.joergdev.mosy.frontend.validation.NotNull;
import de.joergdev.mosy.frontend.validation.StringNotEmpty;
import de.joergdev.mosy.frontend.validation.model.CompareObjects;
import de.joergdev.mosy.frontend.view.TenantV;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import de.joergdev.mosy.shared.Utils;

public class TenantVC extends AbstractViewController<TenantV>
{
  private Tenant tenant;

  private boolean creation;

  public void init()
  {
    new InitExecution().execute();
  }

  private class InitExecution extends Execution
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
      tenant = Utils.nvl(TokenHolder.getTenant(), new Tenant());

      creation = tenant.getTenantId() == null;

      readModel();
    }
  }

  public void saveTenant()
  {
    new SaveExecution().execute();
  }

  private class SaveExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new StringNotEmpty(view.getName(), "name"));

      if (creation)
      {
        addValidation(new StringNotEmpty(view.getPassword(), "password"));
      }

      if (!Utils.isEmpty(view.getPassword()) || Utils.isEmpty(view.getPasswordRepeat()))
      {
        addValidation(new Equals(new CompareObjects(view.getPassword(), view.getPasswordRepeat()), "password", "password_repeat"));
      }
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("tenant"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      updateModel();

      invokeApiCall(apiClient -> apiClient.saveTenant(tenant, getSecretHash()));

      if (!creation)
      {
        TokenHolder.setTenant(tenant);
      }

      cancel();
    }

    private Integer getSecretHash()
    {
      Integer secretHash = null;
      if (!Utils.isEmpty(view.getPassword()))
      {
        secretHash = view.getPassword().hashCode();
      }

      return secretHash;
    }
  }

  public void deleteTenant()
  {
    new DeleteExecution().execute();
  }

  private class DeleteExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(tenant.getTenantId(), "id"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("tenant"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteTenant(tenant.getTenantId()));

      JsfUtils.invalidateSession();

      JsfUtils.redirect(Resources.SITE_GOODBUY);
    }
  }

  private void readModel()
  {
    view.setTenantId(tenant.getTenantId());
    view.setName(tenant.getName());
    view.setDeleteTenantDisabled(tenant.getTenantId() == null);
  }

  private void updateModel()
  {
    tenant.setName(view.getName());
  }

  public void cancel()
  {
    new CancelExecution().execute();
  }

  private class CancelExecution extends Execution
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
      JsfUtils.redirect(creation ? Resources.SITE_LOGIN : Resources.SITE_MAIN);
    }
  }
}
