package com.visoft.file.service.service.user;

import com.visoft.file.service.exception.user.UserAlreadyExistException;
import com.visoft.file.service.persistence.entity.user.User;
import com.visoft.file.service.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElse(null);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean isExistsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public User create(User user) {
        if (isExistsByLogin(user.getLogin())) {
            throw new UserAlreadyExistException();
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByLogin(String email) {
        return userRepository.findByLogin(email);
    }
}
