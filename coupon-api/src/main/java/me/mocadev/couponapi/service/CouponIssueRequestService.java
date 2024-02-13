package me.mocadev.couponapi.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mocadev.couponapi.controller.dto.CouponIssueRequestDto;
import me.mocadev.couponcore.service.AsyncCouponIssueServiceV1;
import me.mocadev.couponcore.service.CouponIssueService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

	private final CouponIssueService couponIssueService;
	private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;

	public void issueRequestV1(CouponIssueRequestDto requestDto) {
		couponIssueService.issue(requestDto.couponId(), requestDto.userId());
		log.info("쿠폰 발급 완료. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
	}

	public void asyncIssueRequestV1(CouponIssueRequestDto requestDto) {
		asyncCouponIssueServiceV1.issue(requestDto.couponId(), requestDto.userId());
		log.info("쿠폰 발급 완료. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
	}
}
