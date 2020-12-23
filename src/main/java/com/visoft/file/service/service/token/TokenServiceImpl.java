package com.visoft.file.service.service.token;

import com.visoft.file.service.configuration.TokenHandler;
import com.visoft.file.service.persistence.entity.Token;
import com.visoft.file.service.persistence.entity.user.User;
import com.visoft.file.service.persistence.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final TokenHandler tokenHandler;
    @Value("${jwt.exp.hours}")
    private Long tokenExpirationTime;

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public Optional<Token> findByUser(User user) {
        return tokenRepository.findByUserId(user.getId());
    }

    @Override
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public long delete(Token token) {
        tokenRepository.delete(token);
        return 1L;
    }

    @Override
    public long deleteByUser(User user) {
        tokenRepository.deleteByUserId(user.getId());
        return 1L;
    }

    @Override
    public long delete(String id) {
        tokenRepository.deleteById(id);
        return 1L;
    }

    @Override
    public void deleteTokenByUserId(String userId) {
        tokenRepository.deleteByUserId(userId);
    }

    @Override
    public long deleteToken(Token token) {
        return Optional.ofNullable(token)
                .map(Token::getId)
                .flatMap(this::findById)
                .map(Token::getId)
                .map(this::delete)
                .orElse(0L);
    }

    @Override
    public Token createToken(User user) {
        final Token token = new Token(
                makeExpirationPoint(),
                tokenHandler.generateAccessToken(user), user.getId()
        );

        return create(token);
    }

    @Override
    public Token create(Token token) {
        return tokenRepository.save(token);
    }

    private Optional<Token> findById(String id) {
        return tokenRepository.findById(id);
    }

    @Override
    public Instant makeExpirationPoint() {
        return Instant.now()
                .plus(tokenExpirationTime, ChronoUnit.HOURS);
    }
}
