package com.martinatanasov.usersystem.service;

import com.martinatanasov.usersystem.entity.User;
import com.martinatanasov.usersystem.result.AuthResult;
import com.martinatanasov.usersystem.result.LoginResult;
import com.martinatanasov.usersystem.result.RefreshResult;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class AuthFacadeService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final JWTParser jwtParser;
    private final JWTAuthContextInfo refreshContext;

    @Inject
    public AuthFacadeService(UserService userService,
            PasswordService passwordService,
            TokenService tokenService,
            JWTParser jwtParser,
            JWTAuthContextInfo defaultContext) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.jwtParser = jwtParser;
        // Same keys and issuer as the default config, but expects aud=refresh
        this.refreshContext = new JWTAuthContextInfo(defaultContext);
        this.refreshContext.setExpectedAudience(Set.of(TokenService.REFRESH_AUDIENCE));
    }

    public LoginResult authenticate(final String email, final String password) {
        AuthResult authResult = userService.findByEmailAndFullEnabled(email);
        if (authResult instanceof AuthResult.Success) {
            User user = ((AuthResult.Success) authResult).user();
            if (!passwordService.matches(password, user.getPassword())) {
                return new LoginResult.Failure();
            }
            String accessToken = tokenService.createAccessToken(user);
            String refreshToken = tokenService.createRefreshToken(user.getUserId());
            return new LoginResult.Success(accessToken, refreshToken, user.getUserId());
        }
        return new LoginResult.Failure();
    }

    public RefreshResult refresh(String refreshToken) {
        try {
            JsonWebToken jwt = jwtParser.parse(refreshToken, refreshContext);
            Optional<User> user = userService.findByUserIdAndFullEnabled(jwt.getSubject());
            if (user.isPresent()) {
                return new RefreshResult.Success(tokenService.createAccessToken(user.get()));
            }
            return new RefreshResult.Failure();
        } catch (ParseException e) {
            // throw new NotAuthorizedException(BEARER);
            return new RefreshResult.Failure();
        }
    }

}
