package com.visoft.file.service.util.pageable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Pageable {

    private Sort sort;
    private Page page;
}
