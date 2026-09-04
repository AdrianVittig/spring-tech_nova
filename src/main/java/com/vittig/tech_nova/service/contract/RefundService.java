package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.refund.CreateRefundDto;
import com.vittig.tech_nova.data.dto.refund.RefundDto;
import com.vittig.tech_nova.data.entity.Refund;

import java.util.List;

public interface RefundService {
    RefundDto getRefundById(Long id, String email);

    List<RefundDto> getRefundsByOrderId(Long orderId, String email);

    RefundDto createRefundForOrder(CreateRefundDto createRefundDto, String email);

    Refund getRefundByIdEntity(Long id);

    Refund getRefundByIdEntityForUpdate(Long refundId);

    RefundDto markRefundAsSuccessful(Refund refund);

    void cancelRefund(Long refundId, String email);
}
