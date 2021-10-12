package com.app.web.crypto.api.exceptions;

import com.app.web.crypto.api.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class NotFoundControllerAdvice {

    @ExceptionHandler(value = {ResponseStatusException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse badRequestException(ResponseStatusException e){
        return new ApiResponse(e.getStatus(), e.getReason());
    }
}
