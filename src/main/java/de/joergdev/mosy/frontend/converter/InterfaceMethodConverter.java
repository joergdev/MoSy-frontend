package de.joergdev.mosy.frontend.converter;

import de.joergdev.mosy.api.model.InterfaceMethod;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;

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