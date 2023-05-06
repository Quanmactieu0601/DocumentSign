package study.repository;

import java.util.List;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import study.domain.Authority;

/**
 * Spring Data R2DBC repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends R2dbcRepository<Authority, String> {
    @Query(value = "SELECT c.name" + " FROM Authority c" + " WHERE c.name <> 'ROLE_SUPER_ADMIN'")
    List<String> getAuthoritiesName();
}
