package ttldd.labman.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name cannot be blank")
    private String roleName;

    @NotBlank(message = "Role code cannot be blank")
    private String roleCode;

    @NotBlank(message = "Role description cannot be blank")
    private String description;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'READ_ONLY'")
    private String privileges;


    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> user;

    public void addUser(User object) {
        this.user.add(object);
        object.setRole(this);
    }

}
