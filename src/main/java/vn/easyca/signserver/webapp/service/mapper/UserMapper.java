package vn.easyca.signserver.webapp.service.mapper;

import vn.easyca.signserver.infrastructure.database.jpa.entity.Authority;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link UserEntity} and its DTO called {@link UserDTO}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {

    public List<UserDTO> usersToUserDTOs(List<UserEntity> userEntities) {
        return userEntities.stream()
            .filter(Objects::nonNull)
            .map(this::userToUserDTO)
            .collect(Collectors.toList());
    }

    public UserDTO userToUserDTO(UserEntity userEntity) {
        return new UserDTO(userEntity);
    }

    public List<UserEntity> userDTOsToUsers(List<UserDTO> userDTOs) {
        return userDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::userDTOToUser)
            .collect(Collectors.toList());
    }

    public UserEntity userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(userDTO.getId());
            userEntity.setLogin(userDTO.getLogin());
            userEntity.setFirstName(userDTO.getFirstName());
            userEntity.setLastName(userDTO.getLastName());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setImageUrl(userDTO.getImageUrl());
            userEntity.setActivated(userDTO.isActivated());
            userEntity.setLangKey(userDTO.getLangKey());
            Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            userEntity.setAuthorities(authorities);
            return userEntity;
        }
    }


    private Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<Authority> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities = authoritiesAsString.stream().map(string -> {
                Authority auth = new Authority();
                auth.setName(string);
                return auth;
            }).collect(Collectors.toSet());
        }

        return authorities;
    }

    public UserEntity userFromId(Long id) {
        if (id == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        return userEntity;
    }
}
