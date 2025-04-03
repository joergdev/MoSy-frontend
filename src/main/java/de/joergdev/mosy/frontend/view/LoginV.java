package de.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import de.joergdev.mosy.api.model.Tenant;
import de.joergdev.mosy.frontend.view.controller.LoginVC;
import de.joergdev.mosy.frontend.view.core.AbstractView;

@Named("login")
@ViewScoped
public class LoginV extends AbstractView<LoginVC>
{
  public static final String VIEW_PARAM_NO_AUTH = "no_auth";

  private boolean multiTanencyEnabled = false;
  private final List<Tenant> tenants = new ArrayList<>();
  private Tenant tenantSelected;
  private boolean tenantEditDeletePossible = false;

  private String password;

  // dirty hack for empty ui:include so that message getting displayed on PostConstruct
  private String nullString = null;

  @PostConstruct
  public void init()
  {
    controller.init();
  }

  @Override
  protected LoginVC getControllerInstance()
  {
    LoginVC vc = new LoginVC();
    vc.setView(this);

    return vc;
  }

  public void doLogin()
  {
    invokeWithErrorHandling(controller::doLogin);
  }

  public void onTenantSelect()
  {
    // set visible state of buttons for tenant
    controller.updateComponents();
  }

  public void doCreateTenant()
  {
    invokeWithErrorHandling(controller::doCreateTenant);
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getNullString()
  {
    return nullString;
  }

  public void setNullString(String nullString)
  {
    this.nullString = nullString;
  }

  public List<Tenant> getTenants()
  {
    return tenants;
  }

  public Tenant getTenantSelected()
  {
    return tenantSelected;
  }

  public void setTenantSelected(Tenant tenantSelected)
  {
    this.tenantSelected = tenantSelected;
  }

  public boolean isMultiTanencyEnabled()
  {
    return multiTanencyEnabled;
  }

  public void setMultiTanencyEnabled(boolean multiTanencyEnabled)
  {
    this.multiTanencyEnabled = multiTanencyEnabled;
  }

  public boolean isTenantEditDeletePossible()
  {
    return tenantEditDeletePossible;
  }

  public void setTenantEditDeletePossible(boolean tenantEditDeletePossible)
  {
    this.tenantEditDeletePossible = tenantEditDeletePossible;
  }
}
