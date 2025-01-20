package io.ndk.cordis_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String image;

    @ManyToOne
    private UserEntity owner;

    @OneToMany
    private List<ServerChannelEntity> channels = new ArrayList<>();
}
