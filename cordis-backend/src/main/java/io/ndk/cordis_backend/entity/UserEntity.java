package io.ndk.cordis_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ndk.cordis_backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_entity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, max = 15)
    private String password;

    @Column(name = "user_name", nullable = false, length = 15)
    private String userName;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver",cascade = CascadeType.ALL)
    private List<FriendEntity> responseList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender",cascade = CascadeType.ALL)
    private List<FriendEntity> requestList = new ArrayList<>();

    @Column(nullable = false)
    private UserStatus status;

    private String profileImage;

}
