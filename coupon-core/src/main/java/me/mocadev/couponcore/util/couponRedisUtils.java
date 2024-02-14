package me.mocadev.couponcore.util;

public class couponRedisUtils {

	public static String getIssueRequestKey(long couponId) {
		return "issue.request.couponId=%s".formatted(couponId);
	}

	public static String getIssueRequestQueueKey() {
		return "issue.request";
	}
}
