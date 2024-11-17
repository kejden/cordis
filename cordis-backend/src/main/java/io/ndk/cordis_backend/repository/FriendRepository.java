package io.ndk.cordis_backend.repository;

import io.ndk.cordis_backend.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
}
