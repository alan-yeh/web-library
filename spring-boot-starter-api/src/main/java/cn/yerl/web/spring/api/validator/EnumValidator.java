package cn.yerl.web.spring.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alan on 2017/3/13.
 */
public class EnumValidator implements ConstraintValidator<Enum, String> {
    Enum annotation;
    List<String> values;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.annotation = constraintAnnotation;
        this.values = Arrays.asList(this.annotation.values());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null){
            return this.values.contains(value);
        }else {
            return true;
        }
    }
}
