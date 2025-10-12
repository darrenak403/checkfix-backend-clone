package com.datnguyen.testorderservices.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PageResponse<T> {
    int currentPage;
    int totalPages;
    int pageSize;
    long totalItems;

    @Builder.Default
    private List<T> data = Collections.emptyList();
}
