package me.mocadev.couponapi.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mocadev.couponapi.controller.dto.CouponIssueRequestDto;
import me.mocadev.couponcore.service.CouponIssueService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

	private final CouponIssueService couponIssueService;

	public void issueRequestV1(CouponIssueRequestDto requestDto) {
		couponIssueService.issue(requestDto.couponId(), requestDto.userId());
		log.info("쿠폰 발급 완료. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
	}
}
