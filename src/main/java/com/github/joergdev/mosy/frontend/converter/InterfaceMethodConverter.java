package com.github.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.shared.Utils;

@FacesConverter(value = "InterfaceMethodConverter", forClass = InterfaceMethod.class)
public class InterfaceMethodConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    InterfaceMethod ifcM = new InterfaceMethod();

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

    ifcM.setInterfaceMethodId(Utils.asInteger(id));
    ifcM.setName(name);

    return ifcM;
  }
}