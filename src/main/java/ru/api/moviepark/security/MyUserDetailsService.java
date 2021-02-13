package ru.api.moviepark.security;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.api.moviepark.data.entities.UserCredentialEntity;
import ru.api.moviepark.data.repositories.UserCredentialRepo;

import java.util.List;

import static ru.api.moviepark.config.CacheConfig.USER_CREDENTIAL_CACHE;

@Component
public class MyUserDetailsService implements UserDetailsService {

    private static final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserCredentialRepo userCredentialRepo;

    public MyUserDetailsService(UserCredentialRepo userCredentialRepo) {
        this.userCredentialRepo = userCredentialRepo;
    }

    @Cacheable(cacheNames = USER_CREDENTIAL_CACHE)
    public UserCredentialEntity getElementByEmail(String email) {
        return userCredentialRepo.findById(email).orElse(null);
    }

    @Override
    public final UserDetails loadUserByUsername(final String username) {
        UserCredentialEntity credential = getElementByEmail(username);
        if (credential == null) {
            throw new BadCredentialsException("Invalid user");
        }

        List<GrantedAuthority> auths = AuthorityUtils.createAuthorityList(
                credential.getRolesEntity().getPermissions().split(","));
        return new User(username, encoder.encode(credential.getPassword()), auths);
    }
}
