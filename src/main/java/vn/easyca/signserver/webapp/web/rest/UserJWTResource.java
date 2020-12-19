package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.security.jwt.JWTFilter;
import vn.easyca.signserver.webapp.security.jwt.TokenProvider;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.LoginVM;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserJWTResource {
    private final Logger log = LoggerFactory.getLogger(UserJWTResource.class);
    private final TokenProvider tokenProvider;
    private final AsyncTransactionService asyncTransactionService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserJWTResource(TokenProvider tokenProvider, AsyncTransactionService asyncTransactionService, AuthenticationManagerBuilder authenticationManagerBuilder ) {
        this.tokenProvider = tokenProvider;
        this.asyncTransactionService = asyncTransactionService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        log.info("REST request to authorize : {}", loginVM.getUsername());
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        asyncTransactionService.newThread("/api/authenticate", TransactionType.SYSTEM, Action.LOGIN, Extension.NONE, Method.POST,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
