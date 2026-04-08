package com.alex.ecommerce.service;

import com.alex.ecommerce.dto.PaymentInfo;
import com.alex.ecommerce.dto.Purchase;
import com.alex.ecommerce.dto.PurchaseResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CheckoutService {
    PurchaseResponse placeOrder(Purchase purchase);

    PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;

}
