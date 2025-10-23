package net.partala.tasks_manager.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.partala.tasks_manager.auth.RegistrationRequest;
import net.partala.tasks_manager.tasks.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String email;

    private String password;

    private LocalDateTime registrationDateTime;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @JsonIgnore
    @OneToMany(mappedBy = "assignedUser")
    private List<TaskEntity> tasks;
}
