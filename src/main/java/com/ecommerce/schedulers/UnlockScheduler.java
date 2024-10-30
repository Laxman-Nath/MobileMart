package com.ecommerce.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import com.ecommerce.models.Customer;
import com.ecommerce.services.CustomerService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UnlockScheduler {
	Logger logger = LoggerFactory.getLogger(UnlockScheduler.class);
	@Autowired
	private CustomerService customerService;

	private final SimpMessagingTemplate messagingTemplate;

	@Autowired
	public UnlockScheduler(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@Scheduled(fixedRate = 5000)
	public void unlockAccount() {
		logger.info("we are inside scheduler method");
		List<Customer> customers = customerService.getAllLockedCustomers();
		for (Customer c : customers) {
			if (c.isEnable()) {
				if (!c.isAccountNonLocked() && customerService.isLockedTimeExpired(c)) {
					customerService.resetFailedAttempt(c);
					customerService.unlockAccount(c);
					logger.info("Sending unlock message to /topic/unlockMessage for customer: {}", c.getEmail());
					messagingTemplate.convertAndSend("/topic/unlockMessage",
							"Your account is unlocked.You can now log in.");

				}
			}
		}
	}
}
