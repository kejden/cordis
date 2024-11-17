package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.response.FriendResponse;
import io.ndk.cordis_backend.entity.FriendEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FriendMapperImpl implements Mapper<FriendEntity, FriendResponse> {

    private ModelMapper modelMapper;

    @Override
    public FriendResponse mapTo(FriendEntity friendEntity) {
        return modelMapper.map(friendEntity, FriendResponse.class);
    }

    @Override
    public FriendEntity mapFrom(FriendResponse friendResponse) {
        return modelMapper.map(friendResponse, FriendEntity.class);
    }
}
