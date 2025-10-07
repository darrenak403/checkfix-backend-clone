package com.datnguyen.testorderservices.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class TestResultCreateRequest {
    private String hl7Raw;
    private List<ResultItem> results;

    @Data
    public static class ResultItem {
        private String parameter;
        private String value;
        private String flag;
    }
}