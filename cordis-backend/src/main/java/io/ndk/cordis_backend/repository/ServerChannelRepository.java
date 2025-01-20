package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.ServerChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerChannelRepository extends JpaRepository<ServerChannelEntity, Long> {
    List<ServerChannelEntity> findByServerId(Long serverId);
}
