package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessageEntity, Long> {
    @Query("SELECT m FROM DirectMessageEntity m WHERE m.chatId = :chatId ORDER BY m.timestamp ASC")
    Page<DirectMessageEntity> findByChannelId(@Param("chatId") Long chatId, Pageable pageable);
    List<DirectMessageEntity> findByChatId(Long chatId);
}
