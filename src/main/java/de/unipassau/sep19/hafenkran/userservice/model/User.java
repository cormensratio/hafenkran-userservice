package de.unipassau.sep19.hafenkran.userservice.model;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Data
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private UUID id;

    @NonNull
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String username;

    @NonNull
    @NotEmpty
    private String password;

    @NonNull
    private String email;

    @Column(nullable = false)
    private boolean isAdmin;

    public User(@NonNull @NotEmpty String username, @NonNull @NotEmpty String encodedPassword, @NonNull @NotEmpty String email, boolean isAdmin) {
        this.username = username;
        this.password = encodedPassword;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public static User fromUserCreateDTO(@NonNull @Valid UserCreateDTO userDTO) {
        return new User(userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), userDTO.isAdmin());
    }

    @PrePersist
    private void prePersist() {
        this.id = UUID.randomUUID();
    }
}