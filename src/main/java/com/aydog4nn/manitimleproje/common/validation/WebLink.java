package com.aydog4nn.manitimleproje.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, RECORD_COMPONENT})
@Retention(RUNTIME)
@Constraint(validatedBy = WebLinkValidator.class)
public @interface WebLink {
    String message() default "Bağlantı, kullanıcı bilgisi içermeyen geçerli bir http veya https adresi olmalı.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
