package com.example.spring.boot.schedulers;

import com.example.spring.boot.enums.OrderStatus;
import com.example.spring.boot.model.Order;
import com.example.spring.boot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Her gece 00:00'da çalışır
    public void deleteUnfinishedOrders() {
        log.info("OrderScheduler::deleteUnfinishedOrders started.");

        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(24);

        List<Order> oldOrders = orderRepository.findByOrderStatusAndOrderDateBefore(OrderStatus.PROCESSING, oneDayAgo);
        log.info("OrderScheduler::deleteUnfinishedOrders - oldOrders: {}", oldOrders);

        if (!oldOrders.isEmpty()) {
            log.info("OrderScheduler::deleteUnfinishedOrders - oldOrders is not empty.");
            orderRepository.deleteAll(oldOrders);
            log.info("OrderScheduler::deleteUnfinishedOrders - oldOrders deleted.");
        } else {
            log.info("OrderScheduler::deleteUnfinishedOrders - oldOrders is empty.");
        }
    }
}
