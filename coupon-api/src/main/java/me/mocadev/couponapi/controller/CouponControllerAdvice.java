package me.mocadev.couponapi.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import me.mocadev.couponapi.controller.dto.CouponIssueResponseDto;
import me.mocadev.couponcore.model.CouponIssueException;

@RestControllerAdvice
public class CouponControllerAdvice {

	@ExceptionHandler(CouponIssueException.class)
	public CouponIssueResponseDto couponIssueExceptionHandler(CouponIssueException exception) {
		return new CouponIssueResponseDto(false, exception.getErrorCode().message);
	}
}
