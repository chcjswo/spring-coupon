package me.mocadev.couponconsumer.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import me.mocadev.couponconsumer.TestConfig;
import me.mocadev.couponcore.repository.redis.RedisRepository;
import me.mocadev.couponcore.service.CouponIssueService;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

	@Autowired
	private CouponIssueListener sut;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private RedisRepository redisRepository;

	@MockBean
	private CouponIssueService couponIssueService;

	@BeforeEach
	void clear() {
		Collection<String> keys = redisTemplate.keys("*");
		redisTemplate.delete(keys);
	}

	@DisplayName("쿠폰 발급 Queue에 처리 대상이 없다면 발급을 하지 않는다.")
	@Test
	void issue_1() throws JsonProcessingException {
		// when
		sut.issue();

		// then
		verify(couponIssueService, never()).issue(anyLong(), anyLong());
	}

	@DisplayName("쿠폰 발급 Queue에 처리 대상이 있다면 쿠폰을 발급한다.")
	@Test
	void issue_2() throws JsonProcessingException {
		// given
		long couponId = 1;
		long userId = 1;
		int totalQuantity = Integer.MAX_VALUE;
		redisRepository.issueRequest(couponId, userId, totalQuantity);

		// when
		sut.issue();

		// then
		verify(couponIssueService, times(1)).issue(anyLong(), anyLong());
	}

	@DisplayName("쿠폰 발급 요청 순서에 맞게 처리된다.")
	@Test
	void issue_3() throws JsonProcessingException {
		// given
		long couponId = 1;
		long userId1 = 1;
		long userId2 = 2;
		long userId3 = 3;
		int totalQuantity = Integer.MAX_VALUE;
		redisRepository.issueRequest(couponId, userId1, totalQuantity);
		redisRepository.issueRequest(couponId, userId2, totalQuantity);
		redisRepository.issueRequest(couponId, userId3, totalQuantity);

		// when
		sut.issue();

		// then
		InOrder inOrder = inOrder(couponIssueService);
		inOrder.verify(couponIssueService, times(1)).issue(couponId, userId1);
		inOrder.verify(couponIssueService, times(2)).issue(couponId, userId2);
		inOrder.verify(couponIssueService, times(3)).issue(couponId, userId3);
	}

}
