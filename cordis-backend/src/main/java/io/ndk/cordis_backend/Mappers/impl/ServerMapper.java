package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.entity.ServerEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ServerMapper implements Mapper<ServerEntity, ServerDto> {

    private final ModelMapper mapper;

    @Override
    public ServerDto mapTo(ServerEntity serverEntity) {
        return mapper.map(serverEntity, ServerDto.class);
    }

    @Override
    public ServerEntity mapFrom(ServerDto serverDto) {
        return mapper.map(serverDto, ServerEntity.class);
    }
}
