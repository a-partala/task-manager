package net.partala.task_manager.tasks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.partala.task_manager.users.UserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue
    private Long id;


    private String title;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private UserEntity assignedUser;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime createDateTime;

    private LocalDateTime deadlineDate;

    private LocalDateTime doneDateTime;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
}
