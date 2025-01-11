package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import org.springframework.web.multipart.MultipartFile;

public interface ServerService {
    ServerDto getServerById(Long id);
    ServerDto createServer(ServerCreate dto, String email);
    ServerDto updateServer(Long id, ServerDto dto, String email);
    void deleteServer(Long id, String email);
    String updateServerImage(MultipartFile file, Long id, String email);
}
