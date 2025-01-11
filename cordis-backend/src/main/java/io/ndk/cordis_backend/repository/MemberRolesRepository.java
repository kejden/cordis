package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.MemberRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRolesRepository extends JpaRepository<MemberRolesEntity, Long> {
    Optional<MemberRolesEntity> findByUserIdAndServerId(Long userId, Long serverId);
    List<MemberRolesEntity> findByUserId(Long userId);
    void deleteByUserIdAndServerId(Long userId, Long serverId);
    Boolean existsByUserIdAndServerId(Long userId, Long serverId);
}
