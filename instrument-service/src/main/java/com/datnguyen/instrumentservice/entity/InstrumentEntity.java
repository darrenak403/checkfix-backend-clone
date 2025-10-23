package com.datnguyen.instrumentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "instruments")
public class InstrumentEntity {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("mode")
    private InstrumentMode mode;
}
