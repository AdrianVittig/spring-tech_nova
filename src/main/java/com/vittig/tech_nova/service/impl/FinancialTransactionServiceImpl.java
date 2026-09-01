package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.transaction.FTDto;
import com.vittig.tech_nova.data.dto.transaction.UpdateFTDto;
import com.vittig.tech_nova.data.entity.FinancialTransaction;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.entity.PurchaseOrder;
import com.vittig.tech_nova.data.entity.Refund;
import com.vittig.tech_nova.data.repo.FinancialTransactionRepository;
import com.vittig.tech_nova.data.util.*;
import com.vittig.tech_nova.service.contract.FinancialTransactionService;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import com.vittig.tech_nova.service.exception.InvalidTransactionStatusTransitionException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialTransactionServiceImpl implements FinancialTransactionService {
    private final OrderService orderService;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final ModelMapperUtil modelMapper;

    @Override
    public List<FTDto> getAllTransactions() {
        return modelMapper.mapList(this.financialTransactionRepository.findAll(), FTDto.class);
    }

    @Override
    public FTDto getTransactionById(Long id) {
        return modelMapper.map(this.financialTransactionRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Object not found!")
        ), FTDto.class);
    }

    @Override
    @Transactional
    public FTDto finalizeTransaction(Long id, UpdateFTDto ftDto) {
        FinancialTransaction financialTransaction = this.financialTransactionRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Object not found!")
        );
        if(ftDto.getTransactionStatus() == TransactionStatus.PENDING){
            throw new InvalidTransactionStatusTransitionException("Invalid transaction status transition!");
        }
        if(financialTransaction.getTransactionStatus() == TransactionStatus.PENDING){
            financialTransaction.setTransactionStatus(ftDto.getTransactionStatus());
        }else{
            throw new InvalidTransactionStatusTransitionException("Invalid transaction status transition!");
        }
        return modelMapper.map(this.financialTransactionRepository.save(financialTransaction), FTDto.class);
    }

    @Override
    @Transactional
    public FTDto recordPaymentIncome(Long orderId) {
        FinancialTransaction financialTransaction = new FinancialTransaction();
        if(orderId == null){
            throw new ObjectNotFoundException("Null!");
        }
        financialTransaction.setTransactionType(TransactionType.INCOMING);
        Order order = this.orderService.getOrderByIdEntity(orderId);
        if(order.getOrderStatus() != OrderStatus.PAID){
            throw new ObjectNotFoundException("Order is not paid!");
        }
        if(financialTransactionRepository.existsByOrderIdAndTransactionType(orderId, TransactionType.INCOMING)){
            throw new ObjectNotFoundException("A FT exists already!");
        }
        financialTransaction.setAmount(order.getTotal());
        financialTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        financialTransaction.setTime(LocalDateTime.now());
        financialTransaction.setOrder(order);
        this.financialTransactionRepository.save(financialTransaction);
        return modelMapper.map(financialTransaction, FTDto.class);
    }

    @Override
    @Transactional
    public FTDto recordRefundOutcome(Refund refund) {
        if(refund == null || refund.getRefundStatus() != RefundStatus.PENDING){
            throw new InvalidStatusException("Not valid status");
        }
        if(this.financialTransactionRepository.existsByRefundId(refund.getId())){
            throw new InvalidStatusException("Not valid!");
        }
        FinancialTransaction financialTransaction = new FinancialTransaction();
        financialTransaction.setTransactionType(TransactionType.OUTGOING);
        financialTransaction.setAmount(refund.getAmount());
        financialTransaction.setOrder(refund.getOrder());
        financialTransaction.setRefund(refund);
        financialTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        financialTransaction.setTime(LocalDateTime.now());
        return modelMapper.map(this.financialTransactionRepository.save(financialTransaction), FTDto.class);
    }

    @Override
    @Transactional
    public FTDto recordPurchaseExpense(PurchaseOrder purchaseOrder, BigDecimal totalCost) {
        if(purchaseOrder == null || totalCost == null || totalCost.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidStatusException("Not valid input!");
        }
        if(this.financialTransactionRepository.existsByPurchaseOrderId(purchaseOrder.getId())){
            throw new InvalidStatusException("Already exists!");
        }
        if(purchaseOrder.getStatus() != PurchaseOrderStatus.CREATED){
            throw new InvalidStatusException("Not valid status!");
        }
        FinancialTransaction financialTransaction = new FinancialTransaction();
        financialTransaction.setTransactionType(TransactionType.OUTGOING);
        financialTransaction.setAmount(totalCost);
        financialTransaction.setPurchaseOrder(purchaseOrder);
        financialTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        financialTransaction.setTime(LocalDateTime.now());
        return modelMapper.map(this.financialTransactionRepository.save(financialTransaction), FTDto.class);
    }
}
