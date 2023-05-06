package study.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.domain.SignatureTemplate;

@Repository
public interface SignatureTemplateRepository extends JpaRepository<SignatureTemplate, Long>, SignatureTemplateRepositoryCustom {
    Optional<SignatureTemplate> findOneByUserId(Long userId);

    // TODO: lay ban moi nhat hien tai, tuy nhien se sua lai tim theo trang thai active, moi user chi duoc active 1 mau chu ky tai 1 thoi diem
    Optional<SignatureTemplate> findFirstByUserIdOrderByCreatedDateDesc(Long userId);
    Optional<SignatureTemplate[]> findAllByUserId(Long userId);

    //    @Transactional
    //    @Modifying
    //    @Query(value = "SELECT c.name" +
    //        " FROM Authority c" +
    //        " WHERE c.name <> 'ROLE_SUPER_ADMIN'")
    Optional<SignatureTemplate> findOneByUserIdAndActivated(Long userId, Boolean activated);
}
