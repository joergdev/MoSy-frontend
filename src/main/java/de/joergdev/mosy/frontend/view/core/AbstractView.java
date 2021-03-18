package de.joergdev.mosy.frontend.view.core;

import java.io.Serializable;
import org.apache.log4j.Logger;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;

public abstract class AbstractView<T extends AbstractViewController<?>> implements Serializable
{
  private static final Logger LOG = Logger.getLogger(AbstractView.class);

  protected T controller;

  public AbstractView()
  {
    controller = getControllerInstance();
  }

  public void invokeWithErrorHandling(Runnable r)
  {
    try
    {
      r.run();
    }
    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);

      controller.showErrorMessage("unexpected_error", ex.getMessage());
    }
  }

  public void refresh()
  {
    controller.refresh();
  }

  public void logout()
  {
    controller.logout();
  }

  protected abstract T getControllerInstance();
}
