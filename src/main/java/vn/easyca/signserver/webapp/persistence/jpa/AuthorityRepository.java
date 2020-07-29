package vn.easyca.signserver.webapp.persistence.jpa;

import vn.easyca.signserver.webapp.persistence.entity.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
