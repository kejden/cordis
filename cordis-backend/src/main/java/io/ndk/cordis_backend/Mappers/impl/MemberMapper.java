package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.entity.MemberRolesEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MemberMapper implements Mapper<MemberRolesEntity, MemberRolesDto> {

    private final ModelMapper modelMapper;

    @Override
    public MemberRolesDto mapTo(MemberRolesEntity memberRolesEntity) {
        return modelMapper.map(memberRolesEntity, MemberRolesDto.class);
    }

    @Override
    public MemberRolesEntity mapFrom(MemberRolesDto memberRolesDto) {
        return modelMapper.map(memberRolesDto, MemberRolesEntity.class);
    }
}
