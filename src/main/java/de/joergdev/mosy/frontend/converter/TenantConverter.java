package de.joergdev.mosy.frontend.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;
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
