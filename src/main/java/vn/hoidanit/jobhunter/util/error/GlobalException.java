package vn.hoidanit.jobhunter.util.error;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {
    
    @ExceptionHandler(value = {
        UsernameNotFoundException.class,
        BadCredentialsException.class,
        IdInvalidException.class
    })
    @ResponseBody
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception exception) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(exception.getMessage());
        res.setMessage("Exception occurs ...");
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = {
        NoResourceFoundException.class
    })
    @ResponseBody
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(NoResourceFoundException exception) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError(exception.getMessage());
        res.setMessage("404 Not Found. URL may not exist ...");
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(exception.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList()); 
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.badRequest().body(res);
    }

}