package com.dontworry.admin.domain.repository;

import com.dontworry.admin.controller.dto.RoleCountDto;
import com.dontworry.admin.controller.dto.StatusCountDto;
import com.dontworry.core.domain.user.entity.QUsers;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsersRepositoryImpl implements UsersRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Users> findAllByFilters(
            UserRole role,
            PlanTier plan,
            ActiveStatus status,
            String email,
            Pageable pageable
    ) {
        QUsers u = QUsers.users;

        BooleanBuilder builder = new BooleanBuilder();

        if (role != null) {
            builder.and(u.role.eq(role));
        }
        if (status != null) {
            builder.and(u.isActive.eq(status));
        }
        if (email != null && !email.isBlank()) {
            builder.and(u.email.containsIgnoreCase(email));
        }

        var query = queryFactory
                .selectFrom(u)
                .where(builder);

        long total = query.fetchCount();

        List<Users> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<RoleCountDto> countGroupByRole() {

        QUsers u = QUsers.users;

        return queryFactory
                .select(Projections.constructor(
                        RoleCountDto.class,
                        u.role,
                        u.count()
                ))
                .from(u)
                .groupBy(u.role)
                .fetch();
    }

    @Override
    public List<StatusCountDto> countGroupByStatus() {

        QUsers u = QUsers.users;

        return queryFactory
                .select(Projections.constructor(
                        StatusCountDto.class,
                        u.isActive,
                        u.count()
                ))
                .from(u)
                .groupBy(u.isActive)
                .fetch();
    }
}
