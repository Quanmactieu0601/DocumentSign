package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.domain.UserEntity;

public class UserDropdownDTO {
    private String login;
    private Long id;

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public UserDropdownDTO(Long id, String login ) {
        this.login = login;
        this.id = id;
    }
}
