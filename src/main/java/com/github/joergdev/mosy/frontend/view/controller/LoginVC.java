package com.github.joergdev.mosy.frontend.view.controller;

import com.github.joergdev.mosy.api.response.system.LoginResponse;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.security.TokenHolder;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.validation.StringNotEmpty;
import com.github.joergdev.mosy.frontend.view.LoginV;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;

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
      String paramNoAuth = JsfUtils.getViewParameter(LoginV.VIEW_PARAM_NO_AUTH);

      if (paramNoAuth != null && "TRUE".equals(paramNoAuth.toUpperCase()))
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
      addValidation(new StringNotEmpty(view.getPassword(), "password"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Integer hash = view.getPassword().hashCode();

      LoginResponse response = invokeApiCall(apiClient -> apiClient.systemLogin(hash));

      TokenHolder.setToken(response.getToken());

      JsfUtils.redirect(Resources.SITE_MAIN);
    }
  }
}