package com.ejie.x38.json;

import java.io.Serializable;

public @interface JsonMixin {
    public Class<? extends Serializable> target();
    public Class<?> mixin();
}