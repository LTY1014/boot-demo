package com.lty.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
public class MyWebSocketHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = 
        Collections.synchronizedSet(new HashSet<>());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log("新连接: " + session.getId());
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log("收到消息: " + payload);
        // 广播消息
        sessions.forEach(s -> {
            if (s.isOpen() && !s.equals(session)) {
                try {
                    s.sendMessage(new TextMessage("广播: " + payload));
                } catch (Exception e) {
                    log("发送消息失败: " + e.getMessage());
                }
            }
        });
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log("连接关闭: " + session.getId());
    }
    private void log(String message) {
        System.out.println("[MyWebSocketHandler] " + message);
    }
}
