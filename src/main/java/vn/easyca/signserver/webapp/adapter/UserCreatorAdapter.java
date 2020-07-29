package vn.easyca.signserver.webapp.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.services.CertGenService;
import vn.easyca.signserver.webapp.service.UserService;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

@Component
public class UserCreatorAdapter implements CertGenService.UserCreator {

    @Autowired
    private UserService userService;

    @Override
    public boolean CreateUser(String username, String password, String fullName) {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(username);
            userDTO.setFirstName(fullName);
            userService.createUser(userDTO, password);
            return true;
        }catch (Exception exception){
            return false;
        }
    }
}
