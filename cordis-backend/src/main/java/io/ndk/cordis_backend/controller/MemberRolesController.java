package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.dto.request.CreateMemberRoles;
import io.ndk.cordis_backend.service.MemberRolesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member-roles")
@AllArgsConstructor
public class MemberRolesController {

    private final MemberRolesService memberRolesService;

    @GetMapping("/{serverId}/{userId}")
    public ResponseEntity<MemberRolesDto> getMemberRoles(@PathVariable Long serverId, @PathVariable Long userId) {
        return ResponseEntity.ok(memberRolesService.getMemberRoles(serverId, userId));
    }

    @PostMapping
    public ResponseEntity<MemberRolesDto> createMemberRoles(@RequestBody CreateMemberRoles dto) {
        MemberRolesDto createdMemberRole = memberRolesService.createMemberRoles(dto);
        return ResponseEntity.status(201).body(createdMemberRole);
    }

    @PutMapping
    public ResponseEntity<MemberRolesDto> updateMemberRoles(@RequestBody CreateMemberRoles dto) {
        MemberRolesDto updatedMemberRole = memberRolesService.updateMemberRoles(dto);
        return ResponseEntity.ok(updatedMemberRole);
    }

    @DeleteMapping("/{serverId}/{userId}")
    public ResponseEntity<Void> deleteMemberRoles(@PathVariable Long serverId, @PathVariable Long userId) {
        memberRolesService.deleteMemberRoles(serverId, userId);
        return ResponseEntity.noContent().build();
    }
}
