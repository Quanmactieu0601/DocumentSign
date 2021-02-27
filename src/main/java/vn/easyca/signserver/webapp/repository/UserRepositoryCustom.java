package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.service.dto.UserDropdownDTO;

import java.util.List;

public interface UserRepositoryCustom {
    Page<UserEntity> findByFilter(Pageable pageable, String login, String account, String name, String email, String ownerId, String commonName, String country, String phone);
    List<UserDropdownDTO> getAllUserForDropdown();
}
