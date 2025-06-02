package io.spring.billRun.config;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class SpringApplicationFailedEvent implements ApplicationListener<ApplicationFailedEvent> {

	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		// 메신저 및 메일 알림 발송 코드 작성
		System.out.println(event.getException().getMessage());
	}
}
