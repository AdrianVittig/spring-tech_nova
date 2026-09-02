package com.vittig.tech_nova.controller;

import com.vittig.tech_nova.data.dto.refund.CreateRefundDto;
import com.vittig.tech_nova.data.dto.refund.RefundDto;
import com.vittig.tech_nova.service.contract.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refunds")
public class RefundController {
    private final RefundService refundService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RefundDto createRefund(@Valid @RequestBody CreateRefundDto createRefundDto, Authentication authentication){
        return this.refundService.createRefundForOrder(createRefundDto, authentication.getName());
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRefund(@PathVariable Long id, Authentication authentication){
        this.refundService.cancelRefund(id, authentication.getName());
    }

    @GetMapping("/{id}")
    public RefundDto getRefundById(@PathVariable Long id, Authentication authentication){
        return this.refundService.getRefundById(id, authentication.getName());
    }

    @GetMapping("/order/{id}")
    public List<RefundDto> getAllRefundsForCurrentUser(@PathVariable Long id, Authentication authentication){
        return this.refundService.getRefundsByOrderId(id, authentication.getName());
    }
}
