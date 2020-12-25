package com.visoft.file.service.web;

import com.visoft.file.service.dto.user.UserCreateDto;
import com.visoft.file.service.dto.user.UserOutcomeDto;
import com.visoft.file.service.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @GetMapping("/findById/{id}")
    public ResponseEntity<UserOutcomeDto> findUserById(@PathVariable String id) {
        return null;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<UserOutcomeDto>> findAllUsers() {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<UserOutcomeDto> createUser(UserCreateDto userCreateDto) {
        return null;
    }

    @PutMapping("/update")
    public ResponseEntity<UserOutcomeDto> updateUser(UserUpdateDto userUpdateDto) {
        return null;
    }

    @PostMapping("/recovery/{id}")
    public void recoveryUser(@PathVariable String id) {
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
    }
}
