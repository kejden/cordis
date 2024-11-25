package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageRequest {
    private String sender;
    private String content;
    private Long channelId;
}
