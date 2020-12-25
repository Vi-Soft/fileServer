package com.visoft.file.service.web;

import com.visoft.file.service.dto.folder.FolderOutcomeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/folders")
public class AdminFolderController {

    @GetMapping("/findById/{id}")
    public ResponseEntity<FolderOutcomeDto> findById(@PathVariable String id) {
        return null;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<FolderOutcomeDto>> findAll() {
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {

    }
}
