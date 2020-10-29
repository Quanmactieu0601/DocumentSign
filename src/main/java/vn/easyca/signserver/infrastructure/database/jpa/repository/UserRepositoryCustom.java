package vn.easyca.signserver.infrastructure.database.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;

public interface UserRepositoryCustom {
    Page<UserEntity> findByFilter(Pageable pageable, String login, String account, String name, String email, String ownerId, String commonName, String country, String phone);
}
