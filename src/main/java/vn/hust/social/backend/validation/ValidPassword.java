package vn.hust.social.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid password. Your password must be at least 8 characters long, contain at least one number and have a mixture of uppercase and lowercase letters.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
