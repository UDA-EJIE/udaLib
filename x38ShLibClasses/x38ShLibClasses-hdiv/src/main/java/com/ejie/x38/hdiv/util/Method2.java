package com.ejie.x38.hdiv.util;

import com.ejie.hdiv.util.Method;

public enum Method2 {
    GET(false), HEAD(false), OPTIONS(false), POST(true), PATCH(true), PUT(true), DELETE(true), ANY(false);

    public final boolean isForm;

    Method2(final boolean isForm) {
        this.isForm = isForm;
    }

    public static Method secureValueOf(final String value) {
        try {
            if (value == null) {
                return null;
            }
            // Convertimos el valor en un Method2
            Method2 method2 = valueOf(value.toUpperCase());

            // Convertimos el Method2 en un Method
            switch (method2) {
                case GET:
                    return Method.GET;
                case HEAD:
                    return Method.HEAD;
                case OPTIONS:
                    return Method.OPTIONS;
                case POST:
                    return Method.POST;
                case PATCH:
                    return Method.PATCH;
                case PUT:
                    return Method.PUT;
                case DELETE:
                    return Method.DELETE;
                case ANY:
                    return Method.ANY;
                default:
                    return null;
            }
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}
