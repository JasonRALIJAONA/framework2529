package mg.itu.prom16.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    // Constructor with default value
    String value() default "";
}
