package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessageEntity, Long> {
    @Query("SELECT m FROM DirectMessageEntity m WHERE m.channelId = :channelId ORDER BY m.timestamp ASC")
    Page<DirectMessageEntity> findByChannelId(@Param("channelId") String channelId, Pageable pageable);
}
