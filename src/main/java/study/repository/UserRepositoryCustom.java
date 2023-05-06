package study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.domain.User;

public interface UserRepositoryCustom {
    Page<User> findByFilter(
        Pageable pageable,
        String login,
        String account,
        String name,
        String email,
        String ownerId,
        String commonName,
        String country,
        String phone,
        boolean activated
    );
}
