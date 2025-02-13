package mg.itu.prom16.annotation.auth;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerAuth {
    int level() default 0;
}