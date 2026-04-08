package com.alex.ecommerce.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class ResponseLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // Bao bọc response để có thể lưu nội dung vào cache
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            // Cho phép request đi tiếp qua các filter khác và tới Repository/Controller
            filterChain.doFilter(request, responseWrapper);
        } finally {
            // Sau khi backend xử lý xong, chúng ta tiến hành log
            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();

            // Lấy nội dung response từ cache
            byte[] responseArray = responseWrapper.getContentAsByteArray();
            String responseBody = new String(responseArray, responseWrapper.getCharacterEncoding());

            System.out.println("-------------------------------------------");
            System.out.println("Endpoint: " + method + " " + uri);
            System.out.println("Response Status: " + responseWrapper.getStatus());
            System.out.println("Response Body: " + responseBody);
            System.out.println("-------------------------------------------");

            // QUAN TRỌNG: Phải copy nội dung từ cache trả về lại response thực tế để frontend nhận được dữ liệu
            responseWrapper.copyBodyToResponse();
        }
    }
}
