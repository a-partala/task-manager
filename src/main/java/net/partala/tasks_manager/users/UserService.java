package net.partala.tasks_manager.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserProperties properties;

    public UserService(
            UserRepository repository,
            UserMapper mapper,
            UserProperties properties
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.properties = properties;
    }

    public User getUserById(Long id) {
        return mapper.toDomain(getUserEntityById(id));
    }

    //For service-layer use only
    public UserEntity getUserEntityById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find user with id = " + id
                ));
    }

    public boolean canUserTakeTask(
            Long userId
    ) {
        var userEntity = getUserEntityById(userId);

        return canUserTakeTask(userEntity);
    }

    public boolean canUserTakeTask(
            UserEntity userEntity
    ) {
        var userTasks = userEntity.getTasks();

        return userTasks.size() < properties.getAssignedTasksLimit();
    }

    public User createUser(
            User userToCreate
    ) {
        var userToSave = mapper.toEntity(userToCreate, null);
        userToSave.setRegistrationDateTime(LocalDateTime.now());
        try {
            var savedUser = repository.save(userToSave);

            log.info("User created successfully, id={}", savedUser.getId());
            return mapper.toDomain(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("User with this login already exists");
        }
    }
}
