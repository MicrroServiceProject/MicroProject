package esprit.projet_web.Exception;


import org.springframework.http.HttpStatus;
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

