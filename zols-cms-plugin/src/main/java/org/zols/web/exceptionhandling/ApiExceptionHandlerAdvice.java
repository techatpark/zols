/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.exceptionhandling;

import java.util.List;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author sathish
 */
@ControllerAdvice(basePackages = "org.zols")
public class ApiExceptionHandlerAdvice {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo handleValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();

        ErrorInfo webServiceError = ErrorInfo.build(ErrorInfo.Type.VALIDATION_ERROR, errors.get(0).getObjectName() + " " + errors.get(0).getDefaultMessage());

        return webServiceError;
    }

}
