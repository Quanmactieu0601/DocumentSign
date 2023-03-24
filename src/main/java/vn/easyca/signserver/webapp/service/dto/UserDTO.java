package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.config.Constants;

import vn.easyca.signserver.webapp.domain.Authority;
import vn.easyca.signserver.webapp.domain.UserEntity;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {
    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 200)
    private String firstName;

    @Size(max = 200)
    private String lastName;

    @Size(max = 200)
    private String commonName;

    @Size(max = 200)
    private String organizationName;

    @Size(max = 200)
    private String organizationUnit;

    @Size(max = 200)
    private String stateName;

    @Size(max = 200)
    private String localityName;

    @Size(max = 200)
    private String country;

    @Size(max = 50)
    private String phone;

    @Email
    @Size(max = 254)
    private String email;

    @Size(max = 256)
    private String imageUrl;

    @Size(max = 255)
    private String password;

    private boolean activated = false;

    @Size(min = 2, max = 10)
    private String langKey;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;

    private Set<String> authorities;

    private Boolean remindChangePassword;

    public Boolean getRemindChangePassword() {
        return remindChangePassword;
    }

    public void setRemindChangePassword(Boolean remindChangePassword) {
        this.remindChangePassword = remindChangePassword;
    }

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    private String currentPassword;

    public UserDTO(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.login = userEntity.getLogin();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.email = userEntity.getEmail();
        this.password = userEntity.getPassword();
        this.activated = userEntity.getActivated();
        this.imageUrl = userEntity.getImageUrl();
        this.langKey = userEntity.getLangKey();
        this.createdBy = userEntity.getCreatedBy();
        this.createdDate = userEntity.getCreatedDate();
        this.lastModifiedBy = userEntity.getLastModifiedBy();
        this.lastModifiedDate = userEntity.getLastModifiedDate();
        this.commonName = userEntity.getCommonName();
        this.localityName = userEntity.getLocalityName();
        this.organizationName = userEntity.getOrganizationName();
        this.organizationUnit = userEntity.getOrganizationUnit();
        this.stateName = userEntity.getStateName();
        this.country = userEntity.getCountry();
        this.phone = userEntity.getPhone();
        this.authorities = userEntity.getAuthorities().stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
        this.remindChangePassword = userEntity.getRemindChangePassword();
    }


    public UserDTO(UserEntity userEntity, boolean getAuthority) {
        this.id = userEntity.getId();
        this.login = userEntity.getLogin();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.email = userEntity.getEmail();
        this.activated = userEntity.getActivated();
        this.imageUrl = userEntity.getImageUrl();
        this.langKey = userEntity.getLangKey();
        this.createdBy = userEntity.getCreatedBy();
        this.createdDate = userEntity.getCreatedDate();
        this.lastModifiedBy = userEntity.getLastModifiedBy();
        this.lastModifiedDate = userEntity.getLastModifiedDate();
        this.commonName = userEntity.getCommonName();
        this.localityName = userEntity.getLocalityName();
        this.organizationName = userEntity.getOrganizationName();
        this.organizationUnit = userEntity.getOrganizationUnit();
        this.stateName = userEntity.getStateName();
        this.country = userEntity.getCountry();
        this.phone = userEntity.getPhone();
        this.remindChangePassword = userEntity.getRemindChangePassword();
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            ", commonName=" + commonName +
            ", organizationName=" + organizationName +
            ", organizationUnit=" + organizationUnit +
            ", localityName=" + localityName +
            ", stateName=" + stateName +
            ", country=" + country +
            ", phone=" + phone +
            ", remindChangePassword" + remindChangePassword +
            "}";
    }
}
