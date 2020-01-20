package com.ejie.x38.util;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.TimeZone;

public class CookieLocaleResolver extends org.springframework.web.servlet.i18n.CookieLocaleResolver {

    @Override
    public boolean isCookieSecure() {
        return true;
    }

}