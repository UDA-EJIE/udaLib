/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.control.view;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 *
 * Clase que permite la resolución de vistas en UDA sobreescribiendo la clase de Spring UrlBasedViewResolver
 * para incluir las siguientes propiedades para el acceso a beans mediante Expression Language ${...}.
 * - exposeContextBeansAsAttributes : Permite exponer todos los beans del contexto de Spring 
 * - exposedContextBeanNames : Permite exponer los beans del contexto de Spring definidos como parámetros
 * 
 * @author UDA
 * 
 */
public class UdaViewResolver extends UrlBasedViewResolver {

    private Boolean exposeContextBeansAsAttributes;
    private String[] exposedContextBeanNames;

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }

    public void setExposedContextBeanNames(String[] exposedContextBeanNames) {
        this.exposedContextBeanNames = exposedContextBeanNames;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        AbstractUrlBasedView superView = super.buildView(viewName);
        if (superView instanceof UdaTilesView) {
        	UdaTilesView view = (UdaTilesView) superView;
            if (this.exposeContextBeansAsAttributes != null) view.setExposeContextBeansAsAttributes(this.exposeContextBeansAsAttributes);
            if (this.exposedContextBeanNames != null) view.setExposedContextBeanNames(this.exposedContextBeanNames);
        }
        return superView;
    }

}
