package de.unipassau.sep19.hafenkran.userservice.util;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
            UserCreateDTO admin = new UserCreateDTO("Mortimer", passwordEncoder.encode("test"), "", true);
            userService.registerNewUser(admin);
            UserCreateDTO user = new UserCreateDTO("Rick", passwordEncoder.encode("test"), "", false);
            userService.registerNewUser(user);
        }
    }
}
