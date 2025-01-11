package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.dto.request.CreateMemberRoles;
import io.ndk.cordis_backend.service.MemberRolesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member-roles")
@AllArgsConstructor
public class MemberRolesController {

    private final MemberRolesService memberRolesService;

    @GetMapping("/{serverId}/{userId}")
    public ResponseEntity<MemberRolesDto> getMemberRoles(@PathVariable Long serverId, @PathVariable Long userId) {
        return new ResponseEntity<>(memberRolesService.getMemberRoles(serverId, userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MemberRolesDto> createMemberRoles(@RequestBody CreateMemberRoles dto) {
        MemberRolesDto createdMemberRole = memberRolesService.createMemberRoles(dto);
        return new ResponseEntity<>(createdMemberRole, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<MemberRolesDto> updateMemberRoles(@RequestBody CreateMemberRoles dto) {
        MemberRolesDto updatedMemberRole = memberRolesService.updateMemberRoles(dto);
        return new ResponseEntity<>(updatedMemberRole, HttpStatus.OK);
    }

    @DeleteMapping("/{serverId}/{userId}")
    public ResponseEntity<Void> deleteMemberRoles(@PathVariable Long serverId, @PathVariable Long userId) {
        memberRolesService.deleteMemberRoles(serverId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
