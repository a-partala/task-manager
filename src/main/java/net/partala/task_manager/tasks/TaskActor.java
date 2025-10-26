package net.partala.task_manager.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.partala.task_manager.auth.SecurityUser;
import net.partala.task_manager.users.UserRole;

import java.util.List;

@AllArgsConstructor
@Getter
public class TaskActor {

    Long id;
    List<UserRole> roles;

    public TaskActor(SecurityUser securityUser) {
        id = securityUser.getId();

        roles = securityUser.getAuthorities()
                .stream()
                .map(a ->
                        UserRole.valueOf(
                                a.getAuthority()
                                        .replace("ROLE_", "")
                        )
                )
                .toList();
    }

    public static TaskActor of(SecurityUser securityUser) {
        return new TaskActor(securityUser);
    }

    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN);
    }

    public boolean isCreatorOf(TaskEntity task) {
        return task.getCreator().getId().equals(id);
    }

    public boolean isAssignedTo(TaskEntity task) {
        return task.getAssignedUser() != null &&
                task.getAssignedUser().getId().equals(id);
    }

    public boolean canComplete(TaskEntity task) {

        if(task.getAssignedUser() == null) {
            return false;
        }

        return isAssignedTo(task) || isCreatorOf(task) || isAdmin();
    }

    public boolean canStartTask(TaskEntity task) {

        return isAssignedTo(task) || isCreatorOf(task) || isAdmin();
    }

    public boolean canUpdate(TaskEntity task) {

        return isCreatorOf(task) || isAdmin();
    }
}
