package com.ejie.x38.util;

@Deprecated
public class CookieLocaleResolver extends org.springframework.web.servlet.i18n.CookieLocaleResolver {

    @Override
    public boolean isCookieSecure() {
        return true;
    }

}