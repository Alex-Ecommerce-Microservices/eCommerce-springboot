package com.alex.ecommerce.service;

import com.alex.ecommerce.dao.CustomerRepository;
import com.alex.ecommerce.dto.PaymentInfo;
import com.alex.ecommerce.dto.Purchase;
import com.alex.ecommerce.dto.PurchaseResponse;
import com.alex.ecommerce.entity.Customer;
import com.alex.ecommerce.entity.Order;
import com.alex.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CheckoutServiceImpl(CustomerRepository customerRepository, @Value("${stripe.secret.key}") String secretKey) {
        this.customerRepository = customerRepository;

        // initialize Stripe API with secret key
        Stripe.apiKey = secretKey;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {
        // retrieve the order info from dto
        Set<OrderItem> orderItems = purchase.getOrderItems();

        // generate tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();

        // populate order with orderItems and tracking number
        Order order = purchase.getOrder();
        orderItems.forEach(item -> order.add(item));

        order.setOrderTrackingNumber(orderTrackingNumber);
        order.setShippingAddress(purchase.getShippingAddress());
        order.setBillingAddress(purchase.getBillingAddress());


        // populate customer with order
        Customer customer = purchase.getCustomer();
        String email = purchase.getCustomer().getEmail();
        Customer customerFromDB = customerRepository.findByEmail(email);

        if (customerFromDB != null) {
            customer = customerFromDB;
        }
        customer.addOrder(order);

        // save to the database
        customerRepository.save(customer);

        return new PurchaseResponse(orderTrackingNumber);
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");
        Map<String, Object> parameters = new HashMap<>();

        // log the payment info for debugging
        System.out.println("Creating payment intent with amount: " + paymentInfo.getAmount() + " " + paymentInfo.getCurrency());

        parameters.put("amount", paymentInfo.getAmount());
        parameters.put("currency", paymentInfo.getCurrency());
        parameters.put("payment_method_types", paymentMethodTypes);
        parameters.put("description", "AlexShop purchase");
        parameters.put("receipt_email", paymentInfo.getReceiptEmail());

        return PaymentIntent.create(parameters);
    }

    private String generateOrderTrackingNumber() {
        // use UUID version 4 to generate a random tracking number
        return UUID.randomUUID().toString();
    }
}
