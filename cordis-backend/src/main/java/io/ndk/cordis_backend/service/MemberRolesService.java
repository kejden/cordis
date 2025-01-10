package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.dto.request.CreateMemberRoles;

public interface MemberRolesService {
    MemberRolesDto getMemberRoles(Long serverId, Long userId);
    MemberRolesDto createMemberRoles(CreateMemberRoles memberRolesDto);
    MemberRolesDto updateMemberRoles(CreateMemberRoles memberRolesDto);
    void deleteMemberRoles(Long serverId, Long userId);
}
