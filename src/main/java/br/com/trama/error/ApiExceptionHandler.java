package br.com.trama.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestPartException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problem = build(HttpStatus.BAD_REQUEST,
                "Existem campos inválidos na requisição.", request);
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraint(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex,
                                                            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex,
                                                         HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex,
                                                            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                "O corpo da requisição é inválido. Confira os tipos e valores enviados.", request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ProblemDetail> handleMissingPart(MissingServletRequestPartException ex,
                                                            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Envie o arquivo no campo 'arquivo'.", request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParameter(MissingServletRequestParameterException ex,
                                                                  HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                "O parâmetro obrigatório '" + ex.getParameterName() + "' não foi informado.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                              HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                "O parâmetro '" + ex.getName() + "' possui um formato ou valor inválido.", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                   HttpServletRequest request) {
        return response(HttpStatus.METHOD_NOT_ALLOWED,
                "O método HTTP utilizado não é permitido para este endpoint.", request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleUploadSize(MaxUploadSizeExceededException ex,
                                                           HttpServletRequest request) {
        return response(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo excede o limite de 10 MB.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleIntegrity(DataIntegrityViolationException ex,
                                                          HttpServletRequest request) {
        String cause = String.valueOf(org.springframework.core.NestedExceptionUtils.getMostSpecificCause(ex)
                .getMessage()).toLowerCase();
        if (cause.contains("chk_cenario_impacto") || cause.contains("chk_usuario_perfil")
                || cause.contains("3819")) {
            return response(HttpStatus.BAD_REQUEST,
                    "Um dos valores enviados viola uma regra de domínio do banco de dados.", request);
        }
        if (cause.contains("duplicate") || cause.contains("1062") || cause.contains("unique")) {
            return response(HttpStatus.CONFLICT,
                    "Já existe um registro com os mesmos dados únicos.", request);
        }
        if (cause.contains("foreign key") || cause.contains("1451") || cause.contains("1452")) {
            return response(HttpStatus.CONFLICT,
                    "A operação não pode ser concluída porque existem registros relacionados.", request);
        }
        return response(HttpStatus.CONFLICT,
                "A operação viola uma restrição de integridade do banco de dados.", request);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(Exception ex, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Consulte o log do servidor.", request);
    }

    private ResponseEntity<ProblemDetail> response(HttpStatus status, String detail,
                                                    HttpServletRequest request) {
        return ResponseEntity.status(status).body(build(status, detail, request));
    }

    private ProblemDetail build(HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("about:blank"));
        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", OffsetDateTime.now().toString());
        return problem;
    }
}
