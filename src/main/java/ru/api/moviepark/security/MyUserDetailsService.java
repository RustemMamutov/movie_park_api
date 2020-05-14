package ru.api.moviepark.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.api.moviepark.cache.UserCredentialsTtlCache;
import ru.api.moviepark.data.entities.UserCredentialEntity;

import java.util.List;

@Component
public final class MyUserDetailsService implements UserDetailsService {

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public final UserDetails loadUserByUsername(final String username) {
        if (!UserCredentialsTtlCache.containsElementByEmail(username)) {
            throw new BadCredentialsException("Invalid user");
        }

        UserCredentialEntity credential = UserCredentialsTtlCache.getElementByEmail(username);

        List<GrantedAuthority> auths = AuthorityUtils.createAuthorityList(
                credential.getRolesEntity().getPermissions().split(","));
        return new User(username, encoder.encode(credential.getPassword()), auths);
    }
}
