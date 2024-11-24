package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
}
