package esprit.projet_web.Exception;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(Date date, String message, String description) {
        this.timestamp = date;
        this.message = message;
        this.details = description;
    }

    // Constructeur + Getters
}
