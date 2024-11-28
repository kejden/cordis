package io.ndk.cordis_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectMessageResponse {
    private Long id;
    private Long channelId;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime sendAt;
}
