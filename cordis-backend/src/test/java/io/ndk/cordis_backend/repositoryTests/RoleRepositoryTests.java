package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByName(){
        String name = "admin";
        RoleEntity role = RoleEntity.builder().name(name).build();

        roleRepository.save(role);

        RoleEntity found = roleRepository.findByName(name).orElse(null);

        assertNotNull(found, "Role not found by name");
        assertEquals(found, role);
    }

    @Test
    void testFindByNameNull(){
        assertTrue(roleRepository.findByName("admin").isEmpty(), "Role found");
    }
}
