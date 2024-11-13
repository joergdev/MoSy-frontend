package de.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import de.joergdev.mosy.api.model.Tenant;

@FacesConverter(value = "TenantConverter", forClass = Tenant.class)
public class TenantConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    Tenant tenant = new Tenant();
    tenant.setName(submittedValue.trim());

    return tenant;
  }
}
