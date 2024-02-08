package me.mocadev.couponapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponapi.controller.dto.CouponIssueRequestDto;
import me.mocadev.couponapi.controller.dto.CouponIssueResponseDto;
import me.mocadev.couponapi.service.CouponIssueRequestService;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

	private final CouponIssueRequestService couponIssueRequestService;

	@PostMapping("/v1/issue")
	public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto requestDto) {
		couponIssueRequestService.issueRequestV1(requestDto);
		return new CouponIssueResponseDto(true, null);
	}
}
