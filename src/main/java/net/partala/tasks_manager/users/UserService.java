package net.partala.tasks_manager.users;

import net.partala.tasks_manager.auth.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserProperties properties;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            UserMapper mapper,
            UserProperties properties,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
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

    public void createUser(
            RegistrationRequest registrationRequest
    ) {
        var userToSave = new UserEntity();
        userToSave.setUsername(registrationRequest.username());
        userToSave.setEmail(registrationRequest.email());
        userToSave.setPassword(passwordEncoder.encode(registrationRequest.password()));
        userToSave.setRegistrationDateTime(LocalDateTime.now());

        //first user is admin
        userToSave.setRole(repository.findAny().isEmpty() ?
                UserRole.ADMIN :
                UserRole.USER);

        try {
            var savedUser = repository.save(userToSave);
            log.info("User created successfully, id={}", savedUser.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("This user already exists");
        }
    }
}
