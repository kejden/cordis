package io.ndk.cordis_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String image;

    @ManyToOne
    private UserEntity owner;

    @ManyToMany
    @JoinTable(
            name = "user_servers",
            joinColumns = @JoinColumn(name = "server_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> members;
}
