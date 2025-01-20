package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.entity.ServerChannelEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ServerChannelMapper implements Mapper<ServerChannelEntity, ServerChannelDto> {

    private ModelMapper modelMapper;

    @Override
    public ServerChannelDto mapTo(ServerChannelEntity serverChannelEntity) {
        return modelMapper.map(serverChannelEntity, ServerChannelDto.class);
    }

    @Override
    public ServerChannelEntity mapFrom(ServerChannelDto serverChannelDto) {
        return modelMapper.map(serverChannelDto, ServerChannelEntity.class);
    }
}
