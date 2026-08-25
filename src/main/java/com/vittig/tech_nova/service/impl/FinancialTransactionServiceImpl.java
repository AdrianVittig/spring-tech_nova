package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.transaction.FTDto;
import com.vittig.tech_nova.data.dto.transaction.UpdateFTDto;
import com.vittig.tech_nova.data.entity.FinancialTransaction;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.repo.FinancialTransactionRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.data.util.TransactionStatus;
import com.vittig.tech_nova.data.util.TransactionType;
import com.vittig.tech_nova.service.contract.FinancialTransactionService;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.exception.InvalidTransactionStatusTransitionException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if(financialTransactionRepository.existsByOrderId(orderId)){
            throw new ObjectNotFoundException("A FT exists already!");
        }
        financialTransaction.setAmount(order.getTotal());
        financialTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        financialTransaction.setTime(LocalDateTime.now());
        financialTransaction.setOrder(order);
        this.financialTransactionRepository.save(financialTransaction);
        return modelMapper.map(financialTransaction, FTDto.class);
    }
}
