package net.partala.task_manager.tasks;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
            SELECT t FROM TaskEntity t
            WHERE t.assignedUser.id = :userId
            """)
    List<TaskEntity> getAllTasksAssignedToUser(
            @Param("userId") Long assignedUserId
    );

    @Query("""
            SELECT t FROM TaskEntity t
            WHERE (:creatorId IS NULL OR :creatorId = t.creator.id)
            AND (:assignedUserId IS NULL OR :assignedUserId = t.assignedUser.id)
            AND (:status IS NULL OR :status = t.status)
            AND (:priority IS NULL OR :priority = t.priority)
            """)
    List<TaskEntity> searchAllByFilter(
            @Param("creatorId") Long creatorId,
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            Pageable pageable
    );
}
