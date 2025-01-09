package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.FileService;
import io.ndk.cordis_backend.service.ServerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
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
        return mapper.mapTo(serverRepository.save(serverEntity));
    }

    @Override
    public ServerDto updateServer(Long id, ServerDto dto) {
        ServerEntity serverEntity = serverRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        );
        serverEntity.setName(dto.getName());
        serverRepository.save(serverEntity);
        return mapper.mapTo(serverRepository.save(serverEntity));
    }

    @Override
    public void deleteServer(Long id) {
        if(serverRepository.existsById(id)) {
            serverRepository.deleteById(id);
        }else{
            throw new CustomException(BusinessErrorCodes.NO_SUCH_SERVER);
        }
    }

    @Override
    public String updateServerImage(MultipartFile file, Long id) {
        ServerEntity serverEntity = serverRepository.findById(id).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_SERVER)
        );
        String imagePath = fileService.updateFile(file, serverEntity.getName());
        serverEntity.setImage(imagePath);
        serverRepository.save(serverEntity);
        return cdnBaseUrl+imagePath;
    }
}