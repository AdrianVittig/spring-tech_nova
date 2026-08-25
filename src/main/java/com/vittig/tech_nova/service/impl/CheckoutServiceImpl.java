package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.budget.BudgetDto;
import com.vittig.tech_nova.data.dto.checkout.CheckoutDto;
import com.vittig.tech_nova.data.dto.invoice.InvoiceDto;
import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;
import com.vittig.tech_nova.data.entity.Payment;
import com.vittig.tech_nova.service.contract.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final FinancialTransactionService financialTransactionService;
    private final BudgetService budgetService;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public CheckoutDto processPayment(CreatePaymentDto createPaymentDto) {
        CheckoutDto checkoutDto = new CheckoutDto();
        Payment payment = this.paymentService.createPaymentEntity(createPaymentDto);
        this.orderService.markOrderAsPaid(payment.getOrder());
        this.financialTransactionService.recordPaymentIncome(payment.getOrder().getId());
        BudgetDto budgetDto = this.budgetService.increaseBalance(payment.getOrder().getTotal());
        InvoiceDto invoice = this.invoiceService.createInvoiceForOrder(payment.getOrder().getId());
        checkoutDto.setInvoiceId(invoice.getId());
        checkoutDto.setOrderId(payment.getOrder().getId());
        checkoutDto.setPaymentId(payment.getId());
        checkoutDto.setBalanceAfterPayment(budgetDto.getBalance());
        return checkoutDto;
    }
}
