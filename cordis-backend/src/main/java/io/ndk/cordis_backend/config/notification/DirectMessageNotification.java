package io.ndk.cordis_backend.config.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectMessageNotification {
    private Long id;
    private String sender;
    private String receiver;
    private String content;

}
