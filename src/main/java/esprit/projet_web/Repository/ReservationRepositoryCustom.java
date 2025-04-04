package esprit.projet_web.Repository;


import esprit.projet_web.Entity.ReservationParStatut;
import org.springframework.data.mongodb.repository.Aggregation;

import java.util.List;

public interface ReservationRepositoryCustom {
    @Aggregation(pipeline = {
            "{ $group: { _id: '$statut', count: { $sum: 1 } } }",
            "{ $project: { statut: '$_id', count: 1, _id: 0 } }"
    })
    List<ReservationParStatut> countByStatut();

}

