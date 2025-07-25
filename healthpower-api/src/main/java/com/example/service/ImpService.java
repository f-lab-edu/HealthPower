package com.example.service;

import com.example.entity.User;
import com.example.entity.board.Product;
import com.example.entity.coupon.CouponIssuance;
import com.example.entity.iamport.ImpOrder;
import com.example.entity.iamport.ImpPayment;
import com.example.entity.iamport.OrderCreateRequest;
import com.example.entity.iamport.OrderCreateResponse;
import com.example.entity.payment.TransactionHistory;
import com.example.enumpackage.CouponStatus;
import com.example.enumpackage.TransactionType;
import com.example.repository.*;
import com.example.repository.iamport.ImpOrderRepository;
import com.example.repository.iamport.ImpPaymentRepository;
import com.example.vo.IamportPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImpService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ImpPaymentRepository impPaymentRepository;
    private final ImpOrderRepository impOrderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentLogService paymentLogService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final CouponRepository couponRepository;

    //실서버 반영 시 수정
    private final String API_KEY = "0721110655237464";
    private final String API_SECRET = "iXZZvSjuDnP4YFy4X9HIIEUI7vB0hzvaMwGw2ORMurRyQpMsjrFHV4IAd1Ni84ZzR7IAtNEVeg1FK5nJ";

    public String getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject body = new JSONObject();
        body.put("imp_key", API_KEY);
        body.put("imp_secret", API_SECRET);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.iamport.kr/users/getToken", request, String.class);

        JSONObject json = new JSONObject(response.getBody());
        return json.getJSONObject("response").getString("access_token");
    }

    public OrderCreateResponse createOrder(OrderCreateRequest request, String userId, String merchantUid) {
        int totalAmount = request.getAmount();

        ImpOrder order = new ImpOrder();
        order.setUserId(userId);
        order.setOrderName(request.getOrderName());
        order.setQuantity(request.getQuantity());
        order.setMerchantUid(merchantUid);
        order.setAmount(totalAmount);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        log.info("쿠폰 발급 ID: {}", request.getCouponIssuanceId());

        //쿠폰 발급 정보 연결
        if (request.getCouponIssuanceId() != null) {
            CouponIssuance issuance = couponIssuanceRepository
                    .findById(request.getCouponIssuanceId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다."));
            order.setCouponIssuance(issuance);
        }

        impOrderRepository.save(order);

        return new OrderCreateResponse(merchantUid, totalAmount);
    }

    public IamportPaymentResponse getPaymentInfo(String impUid, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid,
                HttpMethod.GET,
                request,
                String.class
        );

        JSONObject json = new JSONObject(response.getBody()).getJSONObject("response");

        return new IamportPaymentResponse(
                json.getString("imp_uid"),
                json.getString("merchant_uid"),
                json.getInt("amount"),
                json.getString("status")
        );
    }

    public boolean verifyAmount(String merchantUid, int paidAmount) {
        ImpOrder order = impOrderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(()-> new IllegalArgumentException("주문 내역이 존재하지 않음")); // 예: DB에서 조회된 금액
        return order.getAmount() == paidAmount;
    }

    @Transactional
    public String savedPayment(IamportPaymentResponse paymentResponse) {

        String uid = paymentResponse.getMerchantUid();
        log.info("uid:{}, uidLength:{}", uid, uid.length());

        ImpOrder order = impOrderRepository.findByMerchantUid(paymentResponse.getMerchantUid())
                .orElseThrow(() -> new IllegalArgumentException("주문 내역이 존재하지 않음"));
        order.setStatus("PAID");

        ImpPayment impPayment = new ImpPayment();
        impPayment.setImpUid(paymentResponse.getImpUid());
        impPayment.setMerchantUid(paymentResponse.getMerchantUid());
        impPayment.setPaidAmount(paymentResponse.getAmount());
        impPayment.setPayStatus(paymentResponse.getStatus());

        impPaymentRepository.save(impPayment);

        String userId = order.getUserId();
        finalizeIamportPayment(userId, paymentResponse.getMerchantUid(), paymentResponse.getAmount());

        return "/board/product";
    }

    @Transactional
    public void finalizeIamportPayment(String userId, String merchantUid, int paidAmount) {
        // 주문 정보 조회
        ImpOrder order = impOrderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new IllegalArgumentException("주문 내역이 존재하지 않음"));

        // 주문 상태 업데이트
        order.setStatus("PAID");

        // 상품 이름은 merchantUid에서 유추하거나 DB 조회로 별도로 받아와야 함
        String orderName = order.getOrderName(); // 필요시 추가
        int quantity = order.getQuantity();      // 필요시 ImpOrder에 필드 추가

        // 사용자, 상품 조회
        User user = userRepository.findByUserId(userId).orElseThrow();
        Product product = productRepository.findByProductName(orderName)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 없음"));

        Long totalPrice = (long) paidAmount; // 또는 product.getPrice() * quantity

        // 잔액 확인 및 차감
        if (user.getBalance() < totalPrice) {
            paymentLogService.logFailure(userId, totalPrice, user.getBalance(), product, quantity);
            throw new IllegalArgumentException("잔액 부족");
        }

        // 잔액 차감
        user.setBalance(user.getBalance() - totalPrice);
        userRepository.save(user);

        // 상품 재고 차감
        product.decreaseStock(quantity);
        productRepository.save(product);

        //사용한 쿠폰 갯수 차감
        if (order.getCouponIssuance() != null) {
            var usedCoupon = order.getCouponIssuance();

            usedCoupon.setStatus(CouponStatus.USED);
            usedCoupon.setUsedAt(Instant.now());
            couponIssuanceRepository.save(usedCoupon);

            var coupon = usedCoupon.getCoupon();
            if (coupon.getTotalStock() <= 0) {
                throw new IllegalArgumentException("쿠폰 재고가 부족합니다.");
            }

            coupon.setTotalStock(coupon.getTotalStock() - 1);
            couponRepository.save(coupon);
        }

        // 트랜잭션 내역 저장
        transactionHistoryRepository.save(new TransactionHistory(
                userId,
                TransactionType.PAYMENT,
                totalPrice,
                user.getBalance(),
                product.getProductName(),
                quantity
        ));
    }
}
