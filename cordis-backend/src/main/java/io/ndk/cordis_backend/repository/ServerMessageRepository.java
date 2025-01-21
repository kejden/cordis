package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.ServerMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerMessageRepository extends JpaRepository<ServerMessageEntity, Long> {
    List<ServerMessageEntity> findByChatId(Long chatId);
}
