package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserDtoMapper implements Mapper<UserEntity, UserDto> {

    private final ModelMapper mapper;

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return mapper.map(userDto, UserEntity.class);
    }
}
