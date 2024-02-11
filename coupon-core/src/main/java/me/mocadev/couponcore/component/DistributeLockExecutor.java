package me.mocadev.couponcore.component;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributeLockExecutor {

	// private final RedissonClient redissonClient;
	//
	// public void execute(String lockName, long waiteSecond, long leaseSecond, Runnable logic) {
	// 	RLock lock = redissonClient.getLock(lockName);
	// 	try {
	// 		boolean isLocked = lock.tryLock(waiteSecond, leaseSecond, TimeUnit.MILLISECONDS);
	// 		if (!isLocked) {
	// 			throw new IllegalStateException("[" + lockName + "] lock 획득 실패");
	// 		}
	// 		logic.run();
	// 	} catch (InterruptedException e) {
	// 		log.error(e.getMessage(), e);
	// 		throw new RuntimeException(e);
	// 	} finally {
	// 		if (lock.isHeldByCurrentThread()) {
	// 			lock.unlock();
	// 		}
	// 	}
	// }
}
