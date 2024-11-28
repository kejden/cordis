package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageRequest {
    private Long channelId;
    private String sender;
    private String receiver;
    private String content;
}
