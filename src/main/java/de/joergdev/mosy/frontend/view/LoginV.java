package de.joergdev.mosy.frontend.view;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import de.joergdev.mosy.frontend.view.controller.LoginVC;
import de.joergdev.mosy.frontend.view.core.AbstractView;

@ManagedBean("login")
@SessionScoped
public class LoginV extends AbstractView<LoginVC>
{
  public static final String VIEW_PARAM_NO_AUTH = "no_auth";

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
}