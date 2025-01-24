package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.InvitationKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationKeyRepository extends JpaRepository<InvitationKeyEntity, Long> {
    Optional<InvitationKeyEntity> findByInvitationKey(String invitationKey);
    List<InvitationKeyEntity> findByServerId(Long serverId);
    void deleteByServerId(Long serverId);
}
