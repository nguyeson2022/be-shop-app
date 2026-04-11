package com.project.shopapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private int code;
    private String message;
    private Object data;

    public static ResponseDTO success(String message, Object data) {
        return  ResponseDTO.builder()
                .message(message)
                .code(CodeResponse.SUCCESS.getStatus())
                .data(data)
                .build();
    }

    public static ResponseDTO success(String message, CodeResponse code, Object data) {
        return ResponseDTO.builder()
                .message(message)
                .code(code.getStatus())
                .data(data)
                .build();
    }

    public static ResponseDTO success(String message){
        return ResponseDTO.builder()
                .message(message)
                .code(CodeResponse.SUCCESS.getStatus())
                .build();
    }

    public static ResponseDTO success(Object data){
        return ResponseDTO.builder()
                .message(CodeResponse.SUCCESS.getMessage())
                .code(CodeResponse.SUCCESS.getStatus())
                .data(data)
                .build();
    }

    public static ResponseDTO failure(String message){
        return ResponseDTO.builder()
                .message(message)
                .code(CodeResponse.BAD_REQUEST.getStatus())
                .build();
    }

    public static ResponseDTO failure(CodeResponse status){
        return ResponseDTO.builder()
                .message(status.getMessage())
                .code(status.getStatus())
                .build();
    }

    public static ResponseDTO failure(String message, int status){
        return ResponseDTO.builder()
                .message(message)
                .code(status)
                .build();
    }
}
