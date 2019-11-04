package de.unipassau.sep19.hafenkran.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private UUID id;

    @NonNull
    @NotNull
    @Column(nullable = false, unique = true)
    private String username;

    @NonNull
    @NotNull
    private String password;

    @NonNull
    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private boolean isAdmin;

    public User(@NonNull @NotEmpty String username, @NonNull @NotEmpty String encodedPassword, @NonNull @NotEmpty String email, boolean isAdmin) {
        this.username = username;
        this.password = encodedPassword;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    @PrePersist
    private void prePersist() {
        this.id = UUID.randomUUID();
    }
}