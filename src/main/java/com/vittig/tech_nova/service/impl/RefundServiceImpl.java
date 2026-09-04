package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.refund.CreateRefundDto;
import com.vittig.tech_nova.data.dto.refund.CreateRefundItemDto;
import com.vittig.tech_nova.data.dto.refund.RefundDto;
import com.vittig.tech_nova.data.entity.*;
import com.vittig.tech_nova.data.repo.RefundRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.data.util.RefundStatus;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.contract.RefundService;
import com.vittig.tech_nova.service.contract.UserService;
import com.vittig.tech_nova.service.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRepository refundRepository;
    private final ModelMapperUtil modelMapper;
    private final OrderService orderService;
    private final UserService userService;

    @Override
    public RefundDto getRefundById(Long id, String email) {
        Refund refund = this.refundRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Refund not found."));
        if (!Objects.equals(refund.getOrder().getUser().getEmail(), email)) {
            throw new ForbiddenOperationException("You do not have permission to access this refund.");
        }
        return modelMapper.map(refund, RefundDto.class);
    }

    @Override
    public List<RefundDto> getRefundsByOrderId(Long orderId, String email) {
        Order order = this.orderService.getOrderByIdEntity(orderId);
        if (!Objects.equals(order.getUser().getEmail(), email)) {
            throw new ForbiddenOperationException("You do not have permission to access refunds for this order.");
        }
        return modelMapper.mapList(this.refundRepository.findAllRefundsByOrderId(orderId), RefundDto.class);
    }

    @Override
    @Transactional
    public RefundDto createRefundForOrder(CreateRefundDto createRefundDto, String email) {
        if (createRefundDto == null || createRefundDto.getItems() == null || createRefundDto.getItems().isEmpty()) {
            throw new InvalidInputException("Refund must contain at least one item.");
        }
        Order order = this.orderService.getOrderByIdEntityForUpdate(createRefundDto.getOrderId());
        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new InvalidStatusException("Order must be paid before a refund can be requested.");
        }

        User user = this.userService.getUserEntityByEmail(email);

        if (!Objects.equals(order.getUser().getEmail(), email)) {
            throw new ForbiddenOperationException("You do not have permission to request a refund for this order.");
        }

        Refund newRefund = new Refund();
        List<RefundItem> refundItems = new ArrayList<>();
        BigDecimal totalRefundAmount = BigDecimal.ZERO;

        for (CreateRefundItemDto refundItemDto : createRefundDto.getItems()) {
            OrderItem orderItem = order.getOrderItemList().stream()
                    .filter(oI -> oI.getId().equals(refundItemDto.getOrderItemId()))
                    .findFirst().orElseThrow(
                            () -> new ObjectNotFoundException("Order item not found in this order.")
                    );
            if (refundItemDto.getQuantity() == null || refundItemDto.getQuantity() <= 0) {
                throw new InvalidQuantityException("Refund quantity must be greater than zero.");
            }

            Integer alreadyRefundedQuantity = this.refundRepository.findAllRefundsByOrderId(order.getId())
                    .stream().filter(r -> r.getRefundStatus() == RefundStatus.PENDING || r.getRefundStatus() == RefundStatus.SUCCESSFUL)
                    .flatMap(refund -> refund.getRefundItemList().stream())
                    .filter(ri -> ri.getItem().getId().equals(orderItem.getId()))
                    .map(RefundItem::getQuantity).reduce(0, Integer::sum);

            Integer remainingQuantity = orderItem.getQuantity() - alreadyRefundedQuantity;

            if (refundItemDto.getQuantity() > remainingQuantity) {
                throw new InvalidQuantityException("Refund quantity exceeds the remaining refundable quantity.");
            }
            RefundItem refundItem = new RefundItem();
            refundItem.setItem(orderItem);
            refundItem.setQuantity(refundItemDto.getQuantity());
            refundItem.setPriceSnapshot(orderItem.getUnitPriceSnapshot());
            refundItem.setRefund(newRefund);
            refundItems.add(refundItem);
            totalRefundAmount = totalRefundAmount.add(refundItem.getPriceSnapshot().multiply(BigDecimal.valueOf(refundItem.getQuantity())));
        }
        newRefund.setOrder(order);
        newRefund.setRefundItemList(refundItems);
        newRefund.setAmount(totalRefundAmount);
        newRefund.setRefundStatus(RefundStatus.PENDING);
        newRefund.setRefundedAt(null);
        return modelMapper.map(this.refundRepository.save(newRefund), RefundDto.class);
    }

    @Override
    public Refund getRefundByIdEntity(Long id) {
        return this.refundRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Refund not found.")
        );
    }

    @Override
    public Refund getRefundByIdEntityForUpdate(Long refundId) {
        return this.refundRepository.findRefundByIdEntityForUpdate(refundId).orElseThrow(
                () -> new ObjectNotFoundException("Refund not found.")
        );
    }

    @Override
    @Transactional
    public RefundDto markRefundAsSuccessful(Refund refund) {
        if (refund.getRefundStatus() == RefundStatus.PENDING) {
            refund.setRefundStatus(RefundStatus.SUCCESSFUL);
            refund.setRefundedAt(LocalDateTime.now());
            return modelMapper.map(this.refundRepository.save(refund), RefundDto.class);
        } else {
            throw new InvalidStatusException("Only a pending refund can be marked as successful.");
        }
    }

    @Override
    @Transactional
    public void cancelRefund(Long refundId, String email) {
        Refund refund = this.getRefundByIdEntityForUpdate(refundId);
        if (refund.getRefundStatus() != RefundStatus.PENDING) {
            throw new InvalidStatusException("Only a pending refund can be cancelled.");
        }
        if (!refund.getOrder().getUser().getEmail().equals(email)) {
            throw new ForbiddenOperationException("You do not have permission to cancel this refund.");
        }
        refund.setRefundStatus(RefundStatus.CANCELLED);
    }
}
