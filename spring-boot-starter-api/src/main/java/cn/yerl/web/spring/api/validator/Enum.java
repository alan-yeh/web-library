package cn.yerl.web.spring.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by alan on 2017/3/13.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {EnumValidator.class})
public @interface Enum {
    String message() default "";
    String[] values() default {};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
