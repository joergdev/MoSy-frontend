package de.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import de.joergdev.mosy.api.model.InterfaceMethod;

@FacesConverter(value = "InterfaceMethodConverter", forClass = InterfaceMethod.class)
public class InterfaceMethodConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    InterfaceMethod ifcM = new InterfaceMethod();
    ifcM.setName(submittedValue.trim());

    return ifcM;
  }
}