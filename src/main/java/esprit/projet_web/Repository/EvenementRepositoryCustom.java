package esprit.projet_web.Repository;

import esprit.projet_web.Entity.EvenementPopulaire;
import org.springframework.data.mongodb.repository.Aggregation;
import java.util.List;

public interface EvenementRepositoryCustom {
    @Aggregation(pipeline = {
            "{ $lookup: { from: 'reservations', localField: '_id', foreignField: 'evenement.$id', as: 'reservations' } }",
            "{ $project: { id: '$_id', nom: 1, reservationsCount: { $size: '$reservations' } } }",
            "{ $sort: { reservationsCount: -1 } }",
            "{ $limit: 5 }"
    })
    List<EvenementPopulaire> findTop5ByReservationsCount();
}