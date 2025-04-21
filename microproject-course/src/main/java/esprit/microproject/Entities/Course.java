package esprit.microproject.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "courses")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    /**
     * Just the user‑service’s instructor ID
     */
    private Long instructorId;

    /**
     * A set of user IDs (students) from the user‑service
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "course_students",
            joinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Long> studentIds = new HashSet<>();

    /**
     * One Course ⟶ many Materials (cascaded/detached with the course)
     */
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Material> materials = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
