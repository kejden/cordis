package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
    @NotBlank(message = "ChatID of message is required!")
    private Long chatId;
    @NotBlank(message = "UserID of message is required!")
    private Long userId;
    @Size(min = 1, message = "Message content must have at least 1 characters!")
    @Size(max = 255, message = "Message content can have at most 255 characters!")
    @NotBlank(message = "Content of message is required!")
    private String content;
    private Boolean group;
}
