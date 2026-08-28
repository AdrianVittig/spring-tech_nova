package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.refund.CreateRefundDto;
import com.vittig.tech_nova.data.dto.refund.RefundDto;
import com.vittig.tech_nova.data.entity.Refund;

import java.util.List;
import java.util.Optional;

public interface RefundService {
    RefundDto getRefundById(Long id);
    List<RefundDto> getRefundsByOrderId(Long orderId);
    RefundDto createRefundForOrder(CreateRefundDto createRefundDto);
    Refund getRefundByIdEntity(Long id);
    Refund getRefundByIdEntityForUpdate(Long refundId);
    RefundDto markRefundAsSuccessful(Refund refund);
    void cancelRefund(Long refundId);
}
