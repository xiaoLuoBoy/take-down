package com.horqian.api.exception;


import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author bz
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    /**
     * 表单格式
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public Result errorHandler(BindException e) {

        var bindingResult = e.getBindingResult();
        var msg = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));

        return ResultFactory.error(msg);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result errorHandler(MethodArgumentNotValidException e) {
        var bindingResult = e.getBindingResult();
        var msg = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));

        return ResultFactory.error(msg);
    }


    @ExceptionHandler(value = Throwable.class)
    public Result errorHandler(Throwable throwable) {

        log.error("error", throwable);

        return ResultFactory.error(throwable.getMessage());
    }


    @ExceptionHandler(value = BaseException.class)
    public Result errorHandler(BaseException e) {
        return ResultFactory.error(e.getMessage());
    }

    @ExceptionHandler(value = AuthException.class)
    public Result errorHandler(AuthException e) {
        return ResultFactory.unLogin();
    }
}
