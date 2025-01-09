package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<ServerEntity, Long> {
//    int getSizeofMemebersByServerId(Long id);
}
