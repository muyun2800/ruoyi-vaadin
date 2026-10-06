package com.ruoyi.framework.security.vaadin;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class VaadinSecurityService {

    private final AuthenticationContext authenticationContext;

    public VaadinSecurityService(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
    }


    public String  getPrincipalName() {
        return authenticationContext.getPrincipalName().get();
    }
    public void logout() {
        authenticationContext.logout();
    }
}