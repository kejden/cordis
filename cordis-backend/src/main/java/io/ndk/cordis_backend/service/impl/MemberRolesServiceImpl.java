package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.dto.request.CreateMemberRoles;
import io.ndk.cordis_backend.entity.MemberRolesEntity;
import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.MemberRolesRepository;
import io.ndk.cordis_backend.repository.RoleRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.MemberRolesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MemberRolesServiceImpl implements MemberRolesService {

    private final MemberRolesRepository memberRolesRepository;
    private final Mapper<MemberRolesEntity, MemberRolesDto> mapper;
    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final RoleRepository roleRepository;

//    @Override
//    public MemberRolesDto getMemberRoles(Long serverId, Long userId) {
//        return mapper.mapTo(memberRolesRepository.findByUserIdAndServerId(userId, serverId)
//                .orElseThrow(
//                    () -> new CustomException(BusinessErrorCodes.NO_MEMBER)
//                )
//        );
//    }

    @Override
    public MemberRolesDto createMemberRoles(CreateMemberRoles dto) {
        if(memberRolesRepository.existsByUserIdAndServerId(dto.getMemberId(), dto.getServerId())){
            throw new CustomException(BusinessErrorCodes.USER_ROLE_EXISTS);
        }
        MemberRolesEntity memberRolesEntity = MemberRolesEntity.builder()
                .user(getUserEntity(dto.getMemberId()))
                .server(getServerEntity(dto.getServerId()))
                .role(getRoleEntity("USER"))
                .build();
        return mapper.mapTo(memberRolesRepository.save(memberRolesEntity));
    }

    @Override
    public MemberRolesDto updateMemberRoles(CreateMemberRoles dto) {
        MemberRolesEntity mbrEntity = memberRolesRepository.findByUserIdAndServerId(dto.getMemberId(), dto.getServerId()).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_MEMBER)
        );
        mbrEntity.setRole(getRoleEntity(dto.getRole()));

        return mapper.mapTo(memberRolesRepository.save(mbrEntity));
    }

    @Transactional
    @Override
    public void deleteMemberRoles(Long serverId, Long userId) {
        if(memberRolesRepository.existsByUserIdAndServerId(userId, serverId)){
            memberRolesRepository.deleteByUserIdAndServerId(userId, serverId);
        }else{
            throw new CustomException(BusinessErrorCodes.NO_MEMBER);
        }
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL)
        );
    }

    private ServerEntity getServerEntity(Long serverId) {
        return serverRepository.findById(serverId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        );
    }

    private RoleEntity getRoleEntity(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_ROLE)
        );
    }
}
