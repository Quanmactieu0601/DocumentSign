package vn.easyca.signserver.infrastructure.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.interfaces.UserCreator;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

import java.util.*;

@Component
public class UserCreatorImpl implements UserCreator {

    @Autowired
    private UserApplicationService userApplicationService;

    @Override
    public int CreateUser(String username, String password, String fullName) throws UserCreatorException {
        try {
            Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthoritiesByLogin(username);
            if (userEntity.isPresent())
                return UserCreator.RESULT_EXIST;
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(username);
            userDTO.setFirstName(fullName);
            Set<String> authorities = new HashSet<>();
            authorities.add(AuthoritiesConstants.USER);
            userDTO.setAuthorities(authorities);
            userApplicationService.createUser(userDTO);
            return UserCreator.RESULT_CREATED;
        } catch (Exception ex) {
            throw new UserCreatorException(ex);
        }
    }
}
