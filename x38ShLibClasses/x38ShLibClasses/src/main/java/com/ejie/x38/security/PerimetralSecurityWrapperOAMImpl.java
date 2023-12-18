/*
 * Copyright 2022 E.J.I.E., S.A.
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
package com.ejie.x38.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author UDA
 */
public class PerimetralSecurityWrapperOAMImpl implements
        PerimetralSecurityWrapper {

    public static final String HTTP_NAME = "HTTP_NAME";
    public static final String HTTP_SURNAME = "HTTP_SURNAME";
    public static final String HTTP_FULLNAME = "HTTP_FULLNAME";
    public static final String HTTP_USERNAME = "HTTP_USERNAME";
    public static final String HTTP_NIF = "HTTP_NIF";
    public static final String HTTP_POSITION = "HTTP_POSITION";
    public static final String HTTP_GROUPS = "HTTP_GROUPS";
    public static final String HTTP_SESSION_ID = "HTTP_SESSION_ID";
    public static final String HTTP_LOGOUT = "HTTP_LOGOUT";
    private static final Logger logger = LoggerFactory
            .getLogger(PerimetralSecurityWrapperOAMImpl.class);
    private boolean destroySessionSecuritySystem = false;

    public PerimetralSecurityWrapperOAMImpl() {
    }

    public synchronized String validateSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws SecurityException {

        // Setpoint of the User Session. if the session is not created, it will proceed to create it
        httpRequest.getSession(true);

        //Gets param OAM request
        String name = getOamName(httpRequest);
        String surname = getOamSurname(httpRequest);
        String fullname = getOamFullName(httpRequest);
        String username = getOamUsername(httpRequest);
        String nif = getOamNif(httpRequest);
        String position = getOamPosition(httpRequest);
        String groups = getOamGroups(httpRequest);

        if (name == null || surname == null || fullname == null || username == null || nif == null ||
                position == null || groups == null) {
            return "false";
        } else {
            return "true";
        }
    }

    /* Methods to recovery the credentials data */

    public String getUserConnectedUserName(HttpServletRequest httpRequest) {
        String userName = null;
        Authentication authentication = null;

        // Getting Authentication credentials
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            userName = ((Credentials) authentication.getCredentials()).getUserName();
        } else {
            userName = getOamUsername(httpRequest);
        }

        logger.trace("Connected User's Name is: " + userName);
        return userName;
    }

    public HashMap<String, String> getUserDataInfo(HttpServletRequest httpRequest, boolean isCertificate) {
        // Returning UserDataInfo
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put("fullName", decodeOamHeader(httpRequest.getHeader(HTTP_FULLNAME)));
        userData.put("name", decodeOamHeader(httpRequest.getHeader(HTTP_NAME)));
        userData.put("surname", decodeOamHeader(httpRequest.getHeader(HTTP_SURNAME)));

        logger.trace("Connected User's data is: " + userData.toString());

        return userData;
    }

    public String getUserPosition(HttpServletRequest httpRequest) {
        String userPosition = null;
        Authentication authentication = null;

        // Getting Authentication credentials
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            userPosition = ((Credentials) authentication.getCredentials()).getPosition();
        } else {
            userPosition = getOamPosition(httpRequest);
        }

        logger.trace("Connected User's Position is: " + userPosition);
        return userPosition;

    }

    public String getUdaValidateSessionId(HttpServletRequest httpRequest) {
        String udaValidateSessionId = null;

        udaValidateSessionId = (String) httpRequest.getSession(false).getAttribute("udaValidateSessionId");

        // Returning UdaValidateSessionId
        httpRequest.getSession(false).removeAttribute("udaValidateSessionId");

        logger.trace("Connected UserConnectedUidSession is: " + udaValidateSessionId);
        return udaValidateSessionId;
    }

    public Vector<String> getUserInstances(HttpServletRequest httpRequest) {
        Vector<String> userInstances = new Vector<String>();
        Authentication authentication = null;

        // Getting Authentication credentials
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            userInstances = ((Credentials) authentication.getCredentials()).getUserProfiles();
        } else {
            //Bucle para oam.
            String groups = getOamGroups(httpRequest);
            String[] listaGroups = groups.split(":");
            Collections.addAll(userInstances, listaGroups);
        }

        // Returning UserInstances
        logger.trace("Connected UserConnectedUidSession is: " + userInstances);
        return userInstances;

    }

    public String getUserConnectedUidSession(HttpServletRequest httpRequest) {
        String userConnectedUidSession = null;
        Authentication authentication = null;

        // Getting Authentication credentials
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            userConnectedUidSession = ((Credentials) authentication.getCredentials()).getUidSession();
        } else {
            userConnectedUidSession = decodeOamHeader(httpRequest.getHeader(HTTP_SESSION_ID));
        }

        // Si no puede resolver del OAM la cabecera es que algo está pasando con el proxy, mostramos warning
        if (StringUtils.isEmpty(userConnectedUidSession)) {
            logger.warn("The OAM data is not found in the request headers, we need to check that the OAM proxy is " +
                    "working correctly. It will keep retrying until it works or fails with a TOO_MANY_REDIRECTS.");
        }

        logger.trace("Connected UserConnectedUidSession is: " + userConnectedUidSession);
        return userConnectedUidSession;
    }

    public String getPolicy(HttpServletRequest httpRequest) {
        String userPolicy = null;

        userPolicy = (String) httpRequest.getSession(false).getAttribute("policy");

        // Returning UserPosition
        httpRequest.getSession(false).removeAttribute("policy");

        logger.trace("Connected User's Policy is: " + userPolicy);
        return userPolicy;
    }

    public boolean getIsCertificate(HttpServletRequest httpRequest) {
        String userIsCertificate = null;
        boolean userBooleanIsCertificate;

        userIsCertificate = (String) httpRequest.getSession(false).getAttribute("isCertificate");

        userBooleanIsCertificate = "true".equals(userIsCertificate);
        httpRequest.getSession(false).removeAttribute("isCertificate");

        logger.trace("Connected User's isCertificate is: " + userBooleanIsCertificate);
        return userBooleanIsCertificate;
    }

    public String getNif(HttpServletRequest httpRequest) {
        String userNif = null;
        Authentication authentication = null;

        // Getting Authentication credentials
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            userNif = ((Credentials) authentication.getCredentials()).getNif();
        } else {
            userNif = getOamNif(httpRequest);
        }

        logger.trace("Connected User's nif is: " + userNif);
        return userNif;
    }

    /* [END] Methods to recovery the credentials data */

    /**
     * Método para obtener la URL para el sistema de Login, en OAM no es necesario porque
     * el proxy se mete de por medio, por lo tanto devolvemos directamente la URL original
     * a la que se intentaba acceder
     *
     * @param originalURL String con la url original
     * @param ajax        boolean indicando si es una xhr
     * @return String con la url al sistema de login
     */
    @Override
    public String getURLLogin(String originalURL, boolean ajax) {
        logger.debug("Original URLLogin is :" + originalURL);
        logger.debug("URLLogin is: " + originalURL);
        return originalURL;
    }

    /**
     * Método para realizar el borrado de cookies de OAM y las cabeceras de OAM de la request
     *
     * @param httpRequest  HttpServletRequest con la request
     * @param httpResponse HttpServletResponse con la response
     */
    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            //Invalidate HTTP session
            HttpSession httpSession = httpRequest.getSession(false);
            if (httpSession != null) {
                //Cleaning the User Session of Weblogic
                try {
                    logger.info("Session " + httpSession.getId() + " invalidated!");
                    httpSession.invalidate();
                } catch (IllegalStateException e) {
                    logger.info("The user session isn't valid, it is not necessary delete it");
                }
            }

            httpResponse.sendRedirect(httpRequest.getHeader(HTTP_LOGOUT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getDestroySessionSecuritySystem() {
        return this.destroySessionSecuritySystem;
    }

    public void setDestroySessionSecuritySystem(boolean destroySessionSecuritySystem) {
        this.destroySessionSecuritySystem = destroySessionSecuritySystem;
    }

    public Credentials getCredentials() {
        return new UserCredentials();
    }

    /**
     * Internal method to retrieve the OAM groups
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the groups
     */
    private String getOamGroups(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_GROUPS));
    }

    /**
     * Internal method to collect the OAM position
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the position
     */
    private String getOamPosition(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_POSITION));
    }

    /**
     * Internal method to collect the NIF from OAM
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the NIF
     */
    private String getOamNif(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_NIF));
    }

    /**
     * Internal method to collect the OAM username
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the username
     */
    private String getOamUsername(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_USERNAME));
    }

    /**
     * Internal method to collect the fullname from OAM
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the fullname
     */
    private String getOamFullName(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_FULLNAME));
    }

    /**
     * Internal method to collect the surname from OAM
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the surname
     */
    private String getOamSurname(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_SURNAME));
    }

    /**
     * Internal method to retrieve the name from OAM
     *
     * @param httpRequest HttpServletRequest with the request
     * @return String with the name
     */
    private String getOamName(HttpServletRequest httpRequest) {
        return decodeOamHeader(httpRequest.getHeader(HTTP_NAME));
    }

    /**
     * Internal method to decode the header values inputted by OAM
     *
     * @param headerValue String with the header value
     * @return The decoded header value
     */
    private String decodeOamHeader(String headerValue) {
        try {
            if (StringUtils.isNotEmpty(headerValue)) {
                return "" + MimeUtility.decodeText(headerValue);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error decoding from OAM '" + headerValue + "'");
            throw new RuntimeException(e);
        }
    }
}