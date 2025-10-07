package net.partala.tasks_manager.tasks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
            SELECT t FROM TaskEntity t
            WHERE t.assignedUserId = :userId
            """)
    public List<TaskEntity> getAllTasksAssignedToUser(
            @Param("userId") Long assignedUserId
    );
}
