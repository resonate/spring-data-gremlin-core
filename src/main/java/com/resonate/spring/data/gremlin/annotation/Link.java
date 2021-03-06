package com.resonate.spring.data.gremlin.annotation;

import com.tinkerpop.blueprints.Direction;

import java.lang.annotation.*;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Link {

    String value() default "";

    String name() default "";

    Direction direction() default Direction.OUT;
}
