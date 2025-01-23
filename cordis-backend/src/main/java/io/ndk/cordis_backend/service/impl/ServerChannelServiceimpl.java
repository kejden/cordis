package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.entity.*;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.*;
import io.ndk.cordis_backend.service.ServerChannelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ServerChannelServiceimpl implements ServerChannelService {

    private final ServerChannelRepository repository;
    private final UserRepository userRepository;
    private final RoleRepository rolesRepository;
    private final ServerRepository serverRepository;
    private final Mapper<ServerChannelEntity, ServerChannelDto> mapper;
    private final MemberRolesRepository memberRolesRepository;

    @Override
    public List<ServerChannelDto> getByServerId(Long id) {
        return repository.findByServerId(id).stream().map(mapper::mapTo).collect(Collectors.toList());
    }

    @Override
    public ServerChannelDto create(CreateServerChannel dto, String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        MemberRolesEntity role = memberRolesRepository.findByUserIdAndServerId(user.getId(), dto.getServerId()).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_ROLE));

        if(role.getRole().equals(getRoleEntity("MODERATOR")) || role.getRole().equals(getRoleEntity("OWNER"))) {
            ServerChannelEntity saved = repository.save(ServerChannelEntity.builder()
                    .server(serverRepository.findById(dto.getServerId()).orElseThrow(
                            () -> new CustomException(BusinessErrorCodes.NO_SUCH_ID)
                    ))
                    .name(dto.getName())
                    .build());
            return mapper.mapTo(saved);
        }
        throw new CustomException(BusinessErrorCodes.NO_PERMISSION);
    }

    @Override
    public ServerChannelDto update(Long id, CreateServerChannel dto, String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        MemberRolesEntity role = memberRolesRepository.findByUserIdAndServerId(user.getId(), dto.getServerId()).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_ROLE));

        if(role.getRole().equals(getRoleEntity("MODERATOR")) || role.getRole().equals(getRoleEntity("OWNER"))) {
            ServerChannelEntity channelEntity = repository.findById(dto.getServerId()).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_CHANNEL));
            channelEntity.setName(dto.getName());
            return mapper.mapTo(repository.save(channelEntity));

        }
        throw new CustomException(BusinessErrorCodes.NO_PERMISSION);
    }

    @Override
    public void delete(Long id, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));

        ServerChannelEntity channel = repository.findById(id)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_CHANNEL));

        MemberRolesEntity role = memberRolesRepository.findByUserIdAndServerId(user.getId(), channel.getServer().getId())
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_ROLE));

        if (role.getRole().equals(getRoleEntity("MODERATOR")) || role.getRole().equals(getRoleEntity("OWNER"))) {
            repository.deleteById(id);
        } else {
            throw new CustomException(BusinessErrorCodes.NO_PERMISSION);
        }
    }

    private RoleEntity getRoleEntity(String name) {
        return rolesRepository.findByName(name).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_ROLE)
        );
    }
}
