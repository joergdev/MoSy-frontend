package de.joergdev.mosy.frontend.view.core;

import java.io.Serializable;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;

public abstract class AbstractSubView<T extends AbstractView<K>, K extends AbstractViewController<T>>
    implements Serializable
{
  protected T view;
  protected K controller;

  public AbstractSubView(T view)
  {
    this.view = view;

    setControllerFromView();
  }

  private void setControllerFromView()
  {
    if (view != null)
    {
      this.controller = view.controller;
    }
  }
}