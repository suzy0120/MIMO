package com.ssafy.mimo.domain.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketRequestDataDto {
    @Builder.Default
    private String requestName = "request";
    @Builder.Default
    private String color = "default";
    @Builder.Default
    private Integer time = 0;
    @Builder.Default
    private Integer state = 0;
    @Override
    public String toString() {
        return "{\"requestName:\"" + requestName +
                "\", \"color\":'" + color +
                "\", \"time\":" + time +
                ", \"state\":" + state +
                '}';
    }
}
