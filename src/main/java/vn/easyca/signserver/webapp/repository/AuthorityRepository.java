package vn.easyca.signserver.webapp.repository;

import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
