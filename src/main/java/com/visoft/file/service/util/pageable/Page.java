package com.visoft.file.service.util.pageable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Page {

    private Integer number;
    private Integer size;
}
