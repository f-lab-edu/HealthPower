package com.example.HealthPower.customAnnotation;

import com.example.HealthPower.dto.login.JoinDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, JoinDTO> {

    @Override
    public boolean isValid(JoinDTO joinDTO, ConstraintValidatorContext constraintValidatorContext) {

        if (joinDTO.getPassword() == null || joinDTO.getPasswordCheck() == null) {
            return false;
        }
        return joinDTO.getPassword().equals(joinDTO.getPasswordCheck());
    }
}
