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
	private final DistributeLockExecutor distributeLockExecutor;

	@PostMapping("/v1/issue")
	public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto requestDto) {
		distributeLockExecutor.execute(
			"lock_" + requestDto.couponId(),
			10000,
			10000,
			() -> couponIssueRequestService.issueRequestV1(requestDto)
		);
		return new CouponIssueResponseDto(true, null);
	}
}
