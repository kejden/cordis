package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;

import java.util.List;

public interface ServerChannelService {
    List<ServerChannelDto> getByServerId(Long id);
    ServerChannelDto create(CreateServerChannel dto, String email);
    ServerChannelDto update(Long id, CreateServerChannel dto, String email);
    void delete(Long id, String email);
}
