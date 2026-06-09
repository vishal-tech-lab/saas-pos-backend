package com.example.Backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Dto.SubscriptionDto;
import com.example.Backend.Service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/all")
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<SubscriptionDto> getSubscription(@PathVariable Long tenantId) {
        return ResponseEntity.ok(subscriptionService.getSubscription(tenantId));
    }

    @PostMapping("/renew/{tenantId}")
    public ResponseEntity<SubscriptionDto> renewSubscription(@PathVariable Long tenantId) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(tenantId));
    }
}
