package vn.easyca.signserver.webapp.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.business.services.CertGenService;
import vn.easyca.signserver.webapp.jpa.entity.UserEntity;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

import java.util.Optional;

@Component
public class UserCreatorAdapter implements CertGenService.UserCreator {

    @Autowired
    private UserApplicationService userApplicationService;

    @Override
    public int CreateUser(String username, String password, String fullName) throws Exception {
        try {
            Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthoritiesByLogin(username);
            if (userEntity.isPresent())
                return CertGenService.UserCreator.RESULT_EXIST;
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(username);
            userDTO.setFirstName(fullName);
            userApplicationService.createUser(userDTO, password);
            return CertGenService.UserCreator.RESULT_CREATED;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not create user");
        }
    }
}
