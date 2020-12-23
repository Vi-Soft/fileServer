package com.visoft.file.service.service.authorization;

import com.visoft.file.service.configuration.TokenHandler;
import com.visoft.file.service.dto.LoginDto;
import com.visoft.file.service.exception.BadCredentialException;
import com.visoft.file.service.persistence.entity.Token;
import com.visoft.file.service.persistence.entity.user.User;
import com.visoft.file.service.service.token.TokenService;
import com.visoft.file.service.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenHandler tokenHandler;
    private final TokenService tokenService;

    @Override
    public synchronized String login(LoginDto loginDto) {
        User actor = userService.findByLogin(loginDto.getLogin())
                .orElseThrow(BadCredentialException::new);

        validatePassword(actor, loginDto.getPassword());

        Token token = manageToken(actor);
        return tokenService.save(token).getToken();
    }

    @Override
    public Long logout() {
        tokenService.deleteByUser(getActorFromContext());
        return 1L;
    }

    @Override
    public User getActorFromContext() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (User) authentication.getPrincipal())
                .orElseGet(User::new);
    }

    public void validatePassword(User actor, String password) {
        if (!passwordEncoder.matches(password, actor.getPassword())) {
            throw new BadCredentialException();
        }
    }

    private Token manageToken(User user) {
        return tokenService.findByUser(user)
                .map(token -> updateExistedToken(token, user))
                .orElseGet(() -> createNewToken(user));
    }

    private Token updateExistedToken(Token token, User user) {
        token.setToken(tokenHandler.generateAccessToken(user));
        token.setExpiration(tokenService.makeExpirationPoint());
        return token;
    }

    private Token createNewToken(User user) {
        return new Token(
                tokenService.makeExpirationPoint(),
                tokenHandler.generateAccessToken(user),
                user.getId()
        );
    }
}
