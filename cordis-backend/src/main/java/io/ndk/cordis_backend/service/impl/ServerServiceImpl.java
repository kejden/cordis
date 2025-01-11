package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
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
import io.ndk.cordis_backend.service.MemberRolesService;
import io.ndk.cordis_backend.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final MemberRolesRepository mbrRepository;
    private final RoleRepository roleRepository;
    private final FileService fileService;
    private final Mapper<ServerEntity, ServerDto> mapper;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Override
    public ServerDto getServerById(Long id) {
        return mapper.mapTo(serverRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        ));
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