package me.mocadev.couponapi.controller.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = NON_NULL)
public record CouponIssueResponseDto(boolean isSuccess, String message) {
}
