package com.visoft.file.service.service.user;

import com.visoft.file.service.dto.user.UserCreateDto;
import com.visoft.file.service.dto.user.UserOutcomeDto;
import com.visoft.file.service.dto.user.UserUpdateDto;
import com.visoft.file.service.exception.user.UserAlreadyExistException;
import com.visoft.file.service.mapper.UserMapper;
import com.visoft.file.service.persistence.entity.user.User;
import com.visoft.file.service.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

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
    public List<UserOutcomeDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public void recovery(String id) {

    }

    @Override
    public UserOutcomeDto create(UserCreateDto userCreateDto) {
        if (isExistsByLogin(userCreateDto.getLogin())) {
            throw new UserAlreadyExistException();
        }
        return userMapper.toDto(userRepository.save(userMapper.toUser(userCreateDto)));
    }

    @Override
    public UserOutcomeDto update(UserUpdateDto userUpdateDto) {
        return null;
    }

    @Override
    public boolean isExistsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public Optional<User> findByLogin(String email) {
        return userRepository.findByLogin(email);
    }
}
