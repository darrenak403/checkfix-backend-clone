package ttldd.labman.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogEvent {
    String action();
    String description() default "";
}