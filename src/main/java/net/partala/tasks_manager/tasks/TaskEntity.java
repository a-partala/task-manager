package net.partala.tasks_manager.tasks;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue
    private Long id;


    private String title;

    private Long creatorId;

    private Long assignedUserId;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime createDateTime;

    private LocalDateTime deadlineDate;

    private LocalDateTime doneDateTime;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
}
