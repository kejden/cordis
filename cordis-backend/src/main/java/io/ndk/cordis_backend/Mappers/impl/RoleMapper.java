package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.RoleDto;
import io.ndk.cordis_backend.entity.RoleEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RoleMapper implements Mapper<RoleEntity, RoleDto> {

    private final ModelMapper modelMapper;

    @Override
    public RoleDto mapTo(RoleEntity roleEntity) {
        return modelMapper.map(roleEntity, RoleDto.class);
    }

    @Override
    public RoleEntity mapFrom(RoleDto roleDto) {
        return modelMapper.map(roleDto, RoleEntity.class);
    }
}
