package com.member.infra.role;

import com.member.infra.role.jpa.RoleJpaRepository;
import com.role.domain.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"api-member-test"})
@DataJpaTest
public class RoleAndRolePathJpaRepositoryTest {
    @Autowired
    RoleJpaRepository roleJpaRepository;


    @Test
    @DisplayName("룰 저장")
    void role_save() {
        // give
        Role role = new Role("마스터");

        // when
        this.roleJpaRepository.save(role);

        //then
        Assertions.assertTrue(role.getId() > 0);
    }

}
