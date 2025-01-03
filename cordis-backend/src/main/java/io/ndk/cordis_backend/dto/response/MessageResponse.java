package io.ndk.cordis_backend.dto.response;

import io.ndk.cordis_backend.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long chatId;
    private String sender;
    private String content;
    private LocalDateTime sendAt;
}
