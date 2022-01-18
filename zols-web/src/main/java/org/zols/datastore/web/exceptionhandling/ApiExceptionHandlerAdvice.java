package org.zols.datastore.web.exceptionhandling;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author sathish
 */
@ControllerAdvice(basePackages = "org.zols")
public final class ApiExceptionHandlerAdvice {

    /**
     * hanles exception.
     *
     * @param e
     * @return errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public List<ObjectError> handleValidationException(
            final MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors();
    }

}
