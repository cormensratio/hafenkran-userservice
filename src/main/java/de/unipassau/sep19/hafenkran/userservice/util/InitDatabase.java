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

import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InitDatabase implements CommandLineRunner {

    @NonNull
    @NotNull
    private final UserService userService;

    @NonNull
    @NotNull
    private final PasswordEncoder passwordEncoder;

    @Value("${mockdata:true}")
    private Boolean isLoadMockdata;

    @Override
    public void run(String... args) throws Exception {
        if (isLoadMockdata) {
            UserCreateDTO admin = new UserCreateDTO("Mortimer", "test", "", true);
            userService.registerNewUser(admin);
            UserCreateDTO user = new UserCreateDTO("Rick", "test", "", false);
            userService.registerNewUser(user);
        }
    }
}
