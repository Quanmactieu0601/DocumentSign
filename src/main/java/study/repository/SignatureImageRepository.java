package study.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.domain.SignatureImage;

@Repository
public interface SignatureImageRepository extends JpaRepository<SignatureImage, Long> {
    Optional<SignatureImage> findOneByUserId(Long userId);

    Optional<SignatureImage> getSignatureImageById(Long id);
}
