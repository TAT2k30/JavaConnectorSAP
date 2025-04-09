package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResultResponse<TValue> {
    protected TValue body;
    protected String timeStamp;
    protected String message;
    protected Integer statusCode;
    protected HttpStatus status;
}
