package net.jtownson.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(FIELD)
@Retention(CLASS)
public @interface Message {
    String value();
}
