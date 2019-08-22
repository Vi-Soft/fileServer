package com.visoft.file.service.dto;

import com.visoft.file.service.Version;
import lombok.Data;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ReportDto {

    private String email;

    private Version version;

    private String url;

    private String customToken;

    private String projectName;

    private String companyName;

    private String archiveName;

    private List<TaskDto> tasks;

    private List<String> paths = new ArrayList<>();

    public void setPaths(List<String> paths) {
        this.paths =
//                paths==null
//                ?new ArrayList<>()
//                :
                paths
                .stream()
                .map(
                        x->Paths
                                .get(x)
                                .toString()
                ).collect(Collectors.toList());
    }
}