package com.dontworry.admin.domain.repository;

import com.dontworry.core.domain.user.entity.Users;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long>, UsersRepositoryCustom {

    Optional<Users> findByEmail(String email);

    // ===== 카운트(전체/Role/Plan/Status) =====
    long countBy();

}