package com.handson.basic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;


@ControllerAdvice
class GlobalDefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @Autowired
    ObjectMapper om;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("exception:" + e.getMessage() + ", class:" + e.getClass().getName());
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        if (e instanceof DuplicateKeyException) {
            return new ResponseEntity<>(anErrorResponse("Duplicate Id"), HttpStatus.CONFLICT);
        } else if (e instanceof EmptyResultDataAccessException) {
            return new ResponseEntity<>(anErrorResponse("Not Found"), HttpStatus.NOT_FOUND);
            // we raise runtime exceptions for business issues.
        } else if (e instanceof MissingServletRequestParameterException) {
            String res = anErrorResponse(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        } else if (e instanceof AccessDeniedException) {
            String res = anErrorResponse(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        } else if (e instanceof HandsonException) {
            String res = anErrorResponse(e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }
        logger.error(e.getMessage(), e);
        // Otherwise it is unexpected
        return new ResponseEntity<>(anErrorResponse("unexpected problem"), HttpStatus.CONFLICT);
    }

    private String anErrorResponse(String s) {
        RestErrorMessage em = RestErrorMessage.RestErrorMessageBuilder.aRestErrorMessage().error(s).build();
        String res = null;
        try {
            res = om.writeValueAsString(em);
            return res;
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }
}
