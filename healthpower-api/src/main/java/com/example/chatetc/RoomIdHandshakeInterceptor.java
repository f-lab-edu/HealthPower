package com.example.chatetc;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class RoomIdHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String roomId = null;

        if (request instanceof ServletServerHttpRequest servletReq) {
            roomId = servletReq.getServletRequest().getParameter("roomId");
        }
        if (roomId != null) {
            attributes.put("roomId", roomId);   // 이후 StompHeaderAccessor 에서도 꺼내 쓸 수 있음
        }

        attributes.put("roomId", roomId);

        return true;    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }

}
