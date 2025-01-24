package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.entity.InvitationKeyEntity;

import java.util.List;

public interface InvitationKeyService {
    InvitationKeyEntity generateInvitationKey(Long serverId);
    List<InvitationKeyEntity> getActiveInvitationKeys(Long serverId);
    boolean validateInvitationKey(String invitationKey);
    Long getServerIdByInvitationKey(String invitationKey);
}
