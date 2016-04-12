package com.github.rkmk.annotations;

import com.github.rkmk.mapper.TypeFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TypeUse {

    Class<? extends TypeFactory> value();
    Class<?>[] types();
}
