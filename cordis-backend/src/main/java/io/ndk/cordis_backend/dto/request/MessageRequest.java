package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @NotBlank(message = "ChatID of message is required!")
    private Long chatId;
    @NotBlank(message = "UserID of message is required!")
    private Long userId;
    @NotBlank(message = "Content of message is required!")
    private String content;
    private Boolean group;
}
