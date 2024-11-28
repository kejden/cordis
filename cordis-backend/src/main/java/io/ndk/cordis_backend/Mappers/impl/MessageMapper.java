package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.response.DirectMessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class DirectMessageMapper implements Mapper<DirectMessageEntity, DirectMessageResponse> {

    private ModelMapper modelMapper;

    @Override
    public DirectMessageResponse mapTo(DirectMessageEntity directMessageEntity) {
        return modelMapper.map(directMessageEntity, DirectMessageResponse.class);
    }

    @Override
    public DirectMessageEntity mapFrom(DirectMessageResponse directMessageResponse) {
        return modelMapper.map(directMessageResponse, DirectMessageEntity.class);
    }
}
