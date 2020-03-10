package com.github.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.shared.Utils;

@FacesConverter(value = "InterfaceConverter", forClass = Interface.class)
public class InterfaceConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    Interface ifc = new Interface();

    String id = null;
    String name = null;

    int idxID = submittedValue.indexOf("[id:");
    int idxIDEnd = -1;
    if (idxID >= 0)
    {
      idxIDEnd = submittedValue.indexOf("]");
      if (idxIDEnd > idxID)
      {
        id = submittedValue.substring(idxID + 4, idxIDEnd);
      }
    }

    int idxName = submittedValue.indexOf("[name:");
    if (idxName > 0)
    {
      int idxNameEnd = submittedValue.indexOf("]", idxName);
      if (idxNameEnd > idxName)
      {
        name = submittedValue.substring(idxName + 6, idxNameEnd);
      }
    }

    ifc.setInterfaceId(Utils.asInteger(id));
    ifc.setName(name);

    return ifc;
  }
}