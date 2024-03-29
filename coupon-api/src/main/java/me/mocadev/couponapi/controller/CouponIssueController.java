package me.mocadev.couponapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponapi.controller.dto.CouponIssueRequestDto;
import me.mocadev.couponapi.controller.dto.CouponIssueResponseDto;
import me.mocadev.couponapi.service.CouponIssueRequestService;
import me.mocadev.couponcore.component.DistributeLockExecutor;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

	private final CouponIssueRequestService couponIssueRequestService;

	@PostMapping("/v1/issue")
	public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto requestDto) {
		couponIssueRequestService.issueRequestV1(requestDto);
		return new CouponIssueResponseDto(true, null);
	}

	@PostMapping("/v1/issue-async")
	public CouponIssueResponseDto asyncIssueV1(@RequestBody CouponIssueRequestDto requestDto) {
		couponIssueRequestService.asyncIssueRequestV1(requestDto);
		return new CouponIssueResponseDto(true, null);
	}

	@PostMapping("/v2/issue-async")
	public CouponIssueResponseDto asyncIssueV2(@RequestBody CouponIssueRequestDto requestDto) {
		couponIssueRequestService.asyncIssueRequestV2(requestDto);
		return new CouponIssueResponseDto(true, null);
	}
}
