package esprit.projet_web.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvenementPopulaire {
    private String id;
    private String nom;
    private int reservationsCount;
}