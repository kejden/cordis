package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.entity.ServerMessageEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ServerMessageMapper implements Mapper<ServerMessageEntity, MessageResponse> {

    private final ModelMapper modelMapper;

    @Override
    public MessageResponse mapTo(ServerMessageEntity serverMessageEntity) {
        return modelMapper.map(serverMessageEntity, MessageResponse.class);
    }

    @Override
    public ServerMessageEntity mapFrom(MessageResponse messageResponse) {
        return modelMapper.map(messageResponse, ServerMessageEntity.class);
    }
}
