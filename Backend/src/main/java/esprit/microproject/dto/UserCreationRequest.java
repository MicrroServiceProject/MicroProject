package esprit.microproject.dto;

// You can add validation annotations later if needed
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;

public class UserCreationRequest {

    // @NotBlank // Example validation
    private String username;

    // @NotBlank // Example validation
    // @Email    // Example validation
    private String email;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}