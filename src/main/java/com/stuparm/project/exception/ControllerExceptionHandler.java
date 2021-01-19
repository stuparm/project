package com.stuparm.project.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {

        Map<String, Object> map = new HashMap<>();
        map.put(TIMESTAMP, LocalDateTime.now(ZoneId.of("Z")));
        map.put(STATUS, 400);
        map.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        map.put(MESSAGE, exception.getMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(DatabaseStateException.class)
    public ResponseEntity<Object> handleDatabaseStateException(DatabaseStateException exception) {

        Map<String, Object> map = new HashMap<>();
        map.put(TIMESTAMP, LocalDateTime.now(ZoneId.of("Z")));
        map.put(STATUS, 500);
        map.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        map.put(MESSAGE, exception.getMessage() + " - check with developers!!!");

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(PlaneNotAssignedException.class)
    public ResponseEntity<Object> handlePlaneNotAssignedException(PlaneNotAssignedException exception) {

        Map<String, Object> map = new HashMap<>();
        map.put(TIMESTAMP, LocalDateTime.now(ZoneId.of("Z")));
        map.put(STATUS, 400);
        map.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        map.put(MESSAGE, exception.getMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String message = ex.getMessage();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (!errors.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 0; i < errors.size() - 1; i++) {
                messageBuilder.append(errors.get(i).getDefaultMessage()).append(" - ");
            }
            messageBuilder.append(errors.get(errors.size() - 1).getDefaultMessage());
            message = messageBuilder.toString();
        }


        Map<String, Object> map = new HashMap<>();
        map.put(TIMESTAMP, LocalDateTime.now(ZoneId.of("Z")));
        map.put(STATUS, 400);
        map.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        map.put(MESSAGE,message);

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }


}
