-- ============================================
--  claim.lua  ―  쿠폰 재고 차감 + 중복 발급 방지
-- --------------------------------------------
--  KEYS[1] : 재고 키          coupon:stock:{couponId}
--  KEYS[2] : 발급 Set 키      coupon:claimed:{couponId}
--  ARGV[1] : userId (String)
--  반환값  : >=0  차감 후 남은 재고
--           -1   재고 없음
--           -2   이미 발급
-- ============================================

-- 1) 이미 발급된 사용자인가?
if redis.call("SISMEMBER", KEYS[2], ARGV[1]) == 1 then
    return -2
end

-- 2) 현재 재고 조회
local stock = tonumber(redis.call("GET", KEYS[1]) or "-1")
if stock <= 0 then
    return -1
end

-- 3) 재고 감소 + 사용자 발급 기록 (원자적 수행)
redis.call("DECR", KEYS[1])
redis.call("SADD", KEYS[2], ARGV[1])

-- 4) 차감 후 남은 재고 반환 (모니터링용)
return stock - 1
