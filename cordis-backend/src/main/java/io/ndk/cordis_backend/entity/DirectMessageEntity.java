package io.ndk.cordis_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectMessageEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "sender_id", nullable = false)
    private Long senderId;

    @JoinColumn(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String channelId;
}
