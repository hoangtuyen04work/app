package com.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.STACK_TRACE;


@Slf4j
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class ErrorController extends AbstractErrorController {

    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
        // Define the error attribute options you want to include
        // For example, you can include the stack trace for debugging purposes
        ErrorAttributeOptions options = ErrorAttributeOptions.of(STACK_TRACE);

        // Log the error
        log.error("Error occurred: {}", getErrorAttributes(request, options));

        // Get error details
        Map<String, Object> errorAttributes = getErrorAttributes(request, options);
        HttpStatus status = getStatus(request);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addAllObjects(errorAttributes);
        modelAndView.setStatus(status);

        return modelAndView;
    }
}
