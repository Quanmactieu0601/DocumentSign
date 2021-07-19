package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.domain.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    @Query(value = "SELECT c.name" +
        " FROM Authority c" +
        " WHERE c.name <> 'ROLE_SUPER_ADMIN'")
    List<String> getAuthoritiesName();
}
