package de.unipassau.sep19.hafenkran.userservice.util;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InitDatabase implements CommandLineRunner {

    @NonNull
    private final UserService userService;

    @NonNull
    private final PasswordEncoder passwordEncoder;

    @Value("${mockdata:true}")
    private Boolean isLoadMockdata;

    @Override
    public void run(String... args) {
        if (isLoadMockdata) {
            User admin = new User("Mortimer", passwordEncoder.encode("test"), "", true);
            admin.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
            userService.registerNewUser(admin);
            User user = new User("Rick", passwordEncoder.encode("test"), "", false);
            user.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
            userService.registerNewUser(user);
        }
    }
}
