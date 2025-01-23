package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.RoleDto;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import io.ndk.cordis_backend.dto.response.ServerResponse;
import io.ndk.cordis_backend.dto.response.UserRole;
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
import io.ndk.cordis_backend.service.FileService;
import io.ndk.cordis_backend.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final MemberRolesRepository mbrRepository;
    private final RoleRepository roleRepository;
    private final FileService fileService;
    private final Mapper<ServerEntity, ServerDto> mapper;
    private final Mapper<RoleEntity, RoleDto> roleMapper;
    private final Mapper<UserEntity, UserDto> userMapper;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Override
    public ServerDto getServerById(Long id) {
        return mapper.mapTo(serverRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        ));
    }

    @Override
    public RoleEntity getUsersRoleForServer(Long id, String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL)
        );
        return mbrRepository.findByUserIdAndServerId(user.getId(), id).get().getRole();
    }

    @Override
    public List<UserRole> getUsersOfServer(Long id) {
        List<MemberRolesEntity> memberRoles = mbrRepository.findByServerId(id);

        return memberRoles.stream()
                .map(memberRole -> UserRole.builder()
                        .user(userMapper.mapTo(memberRole.getUser()))
                        .role(memberRole.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ServerDto createServer(ServerCreate dto, String email) {
        ServerEntity serverEntity = ServerEntity.builder()
                .name(dto.getName())
                .image(dto.getImage())
                .owner(userRepository.findByEmail(email).orElseThrow(()->new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL)))
                .build();

        ServerEntity saved = serverRepository.save(serverEntity);

        MemberRolesEntity mb = MemberRolesEntity.builder()
                .server(saved)
                .user(getUserEntity(email))
                .role(getRoleEntity("OWNER"))
                .build();

        mbrRepository.save(mb);

        return mapper.mapTo(saved);
    }

    @Override
    public ServerDto updateServer(Long id, ServerDto dto, String email) {
        ServerEntity serverEntity = serverRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        );
        if(serverEntity.getOwner().equals(getUserEntity(email))){
            serverEntity.setName(dto.getName());
            serverRepository.save(serverEntity);
            return mapper.mapTo(serverRepository.save(serverEntity));
        }else{
            throw new CustomException(BusinessErrorCodes.NO_PERMISSION);
        }
    }

    @Override
    public void deleteServer(Long id, String email) {
        if(serverRepository.existsById(id)) {
            ServerEntity serverEntity = serverRepository.findById(id).orElseThrow(
                    () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
            );
            if(serverEntity.getOwner().equals(getUserEntity(email))){
                serverRepository.deleteById(id);
            }else{
                throw new CustomException(BusinessErrorCodes.NO_PERMISSION);
            }
        }else{
            throw new CustomException(BusinessErrorCodes.NO_SUCH_SERVER);
        }
    }

    @Override
    public String updateServerImage(MultipartFile file, Long id, String email) {
        ServerEntity serverEntity = serverRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        );
        if(serverEntity.getOwner().equals(getUserEntity(email))){
            String imagePath = fileService.updateFile(file, serverEntity.getName());
            serverEntity.setImage(imagePath);
            serverRepository.save(serverEntity);
            return cdnBaseUrl+imagePath;
        }else{
            throw new CustomException(BusinessErrorCodes.NO_PERMISSION);
        }
    }

    @Override
    public List<ServerResponse> getAllServerOfUser(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL)
        );
        List<MemberRolesEntity> mbs = mbrRepository.findByUserId(user.getId());
        List<ServerResponse> response = new ArrayList<>();
        for(MemberRolesEntity mb : mbs){
            response.add(
                    ServerResponse.builder()
                            .server(mapper.mapTo(mb.getServer()))
                            .role(roleMapper.mapTo(mb.getRole()))
                            .build());
        }
        return response;
    }

    private UserEntity getUserEntity(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL)
        );
    }

    private RoleEntity getRoleEntity(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_ROLE)
        );
    }
}