package de.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import de.joergdev.mosy.shared.Utils;

public abstract class AbstractConverter implements Converter<Object>
{
  public String getAsString(FacesContext context, UIComponent component, Object modelValue)
  {
    return modelValue == null
        ? ""
        : modelValue.toString();
  }

  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    if (Utils.isEmpty(submittedValue))
    {
      return null;
    }

    return _getAsObject(context, component, submittedValue);
  }

  protected abstract Object _getAsObject(FacesContext context, UIComponent component, String submittedValue);
}