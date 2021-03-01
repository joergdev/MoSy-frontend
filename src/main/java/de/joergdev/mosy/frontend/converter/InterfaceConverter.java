package de.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
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