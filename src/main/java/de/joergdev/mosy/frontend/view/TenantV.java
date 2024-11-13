package de.joergdev.mosy.frontend.view;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import de.joergdev.mosy.frontend.view.controller.TenantVC;
import de.joergdev.mosy.frontend.view.core.AbstractView;

@ManagedBean("tenant")
@ViewScoped
public class TenantV extends AbstractView<TenantVC>
{
  private Integer tenantId;
  private String name;

  private String password;
  private String passwordRepeat;

  private boolean deleteTenantDisabled = true;

  // dirty hack for empty ui:include so that message getting displayed on PostConstruct
  private String nullString = null;

  @PostConstruct
  public void init()
  {
    controller.init();
  }

  @Override
  protected TenantVC getControllerInstance()
  {
    TenantVC vc = new TenantVC();
    vc.setView(this);

    return vc;
  }

  public void saveTenant()
  {
    invokeWithErrorHandling(controller::saveTenant);
  }

  public void deleteTenant()
  {
    invokeWithErrorHandling(controller::deleteTenant);
  }

  public void cancel()
  {
    controller.cancel();
  }

  public String getNullString()
  {
    return nullString;
  }

  public void setNullString(String nullString)
  {
    this.nullString = nullString;
  }

  public Integer getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(Integer tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPasswordRepeat()
  {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String passwordRepeat)
  {
    this.passwordRepeat = passwordRepeat;
  }

  public boolean isDeleteTenantDisabled()
  {
    return deleteTenantDisabled;
  }

  public void setDeleteTenantDisabled(boolean deleteTenantDisabled)
  {
    this.deleteTenantDisabled = deleteTenantDisabled;
  }
}
