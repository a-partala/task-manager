package net.partala.task_manager.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.partala.task_manager.tasks.TaskEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //todo: full check for uniqueness and verifying
    private String email;

    private String password;

    private LocalDateTime registrationDateTime;

    @Column(name = "is_email_verified")
    private boolean emailVerified;

    @CollectionTable
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "assignedUser")
    private Set<TaskEntity> tasks;
}
