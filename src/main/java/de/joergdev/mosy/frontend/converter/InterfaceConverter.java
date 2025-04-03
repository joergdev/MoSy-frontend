package de.joergdev.mosy.frontend.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;
import de.joergdev.mosy.api.model.Interface;

@FacesConverter(value = "InterfaceConverter", forClass = Interface.class)
public class InterfaceConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    Interface ifc = new Interface();
    ifc.setName(submittedValue.trim());

    return ifc;
  }
}