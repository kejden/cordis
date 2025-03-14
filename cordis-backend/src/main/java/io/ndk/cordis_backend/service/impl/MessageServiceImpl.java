package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.Mappers.impl.DirectMessageMapper;
import io.ndk.cordis_backend.Mappers.impl.ServerMessageMapper;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.entity.ServerMessageEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.repository.ServerMessageRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.MessageService;
import lombok.AllArgsConstructor;
import org.apache.catalina.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final DirectMessageRepository messageRepository;
    private final ServerMessageRepository serverMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final DirectMessageMapper mapper;
    private final ServerMessageMapper serverMessagemapper;

    @Override
    public MessageResponse saveMessage(MessageRequest messageDto) {
        if(messageDto.getGroup()){
            ServerMessageEntity message = ServerMessageEntity.builder()
                    .content(messageDto.getContent())
                    .sender(getUser(messageDto.getUserId()))
                    .timestamp(LocalDateTime.now())
                    .chatId(messageDto.getChatId())
                    .build();
            ServerMessageEntity save = serverMessageRepository.save(message);
            MessageResponse mr = serverMessagemapper.mapTo(save);
            messagingTemplate.convertAndSend("/group/" + message.getChatId(), mr);
            return mr;
        }else{
            DirectMessageEntity message = DirectMessageEntity.builder()
                    .content(messageDto.getContent())
                    .sender(getUser(messageDto.getUserId()))
                    .timestamp(LocalDateTime.now())
                    .chatId(messageDto.getChatId())
                    .build();
            DirectMessageEntity savedMessage = messageRepository.save(message);
            MessageResponse messageResponse = mapper.mapTo(savedMessage);
            messagingTemplate.convertAndSend("/user/" + message.getChatId(), messageResponse);
            return messageResponse;
        }

    }

//    @Override
//    public Page<MessageResponse> getMessages(Long channelId, int page, int size) {
//        return messageRepository.findByChannelId(channelId, PageRequest.of(page, size)).map(mapper::mapTo);
//    }

    @Override
    public List<MessageResponse> getMessages(Long chatId) {
        return messageRepository.findByChatId(chatId).stream().map(mapper::mapTo).collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getGroupMessages(Long chatId) {
        return serverMessageRepository.findByChatId(chatId).stream().map(serverMessagemapper::mapTo).collect(Collectors.toList());
    }

    private UserEntity getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
    }

}
