package vn.easyca.signserver.infrastructure.database.jpa.repository;

import vn.easyca.signserver.infrastructure.database.jpa.entity.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
