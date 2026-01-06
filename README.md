## boot-demo



<p align=center>
    <a href="http://gitee.com/liang-tian-yu">Spring Boot案例</a>
</p>
<p align="center">
<a target="_blank" href="http://gitee.com/liang-tian-yu">
    <img src="https://img.shields.io/badge/JDK-1.8+-green" ></img>
    <img src="https://img.shields.io/badge/springboot-2.7.0-green" ></img>
    <img src="https://img.shields.io/badge/mysql-8.0-blue" ></img>
    <img src="https://img.shields.io/badge/MybatisPlus-3.5.1-green" ></img>
    <img src="https://img.shields.io/badge/Knife4j -3.0.3-brightgreen" ></img>
</a></p>



记录SpringBoot的demo用例

[TOC]



## JPA

- 导入依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
```



- yml配置

```
spring:
  jpa:
    hibernate:
      ddl-auto: update
      naming:
        # 驼峰命名
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    show-sql: true
    # 默认引擎为InnoDB
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
```



- **实体类**

 @Entity // 作为 hibernate实体类
 @Table(name = "tb_name") // 配置数据库表的名称,实体类中属性和表中字段的映射关系



- 具体测试看JpaTest





## Knife4j

接口文档



配置详见`Knife4jConfig`



application.yml

```
# 解决swagger和springBoot高版本冲突问题
spring:
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
```





## MybaisPlus

自定义生成主键策略

- 定义主键策略

```
public class CustomIdGenerator implements IdentifierGenerator  {

    @Override
    public Long nextId(Object entity) {
        String serialId = SerialUtil.generateSerial();
        return Long.valueOf(serialId);
    }
}
```



- 注入

```
@Configuration
@MapperScan({"com.lty.mapper","com.lty.*.mapper"})
public class MybatisPlusConfig {

    //@Bean
    //public IdentifierGenerator identifierGenerator() {
    //    return new CustomIdGenerator();
    //}
}

```



- 注解使用

```
@TableId(type = IdType.ASSIGN_ID, value = "id")
private String id;
```





[油猴脚本](https://juejin.cn/post/7517081861975277603)



- websocket
- spring-security
- boot-test
- antdesign  tree树使用



## TreeUtil

```plain
package com.lty.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Description: 树操作方法工具类
 * @author lty
 */
public class TreeUtil {
    /**
     * 使用Map合成树
     *
     * @param menuList       需要合成树的List
     * @param pId            对象中的父ID字段,如:Menu:getPid
     * @param id             对象中的id字段 ,如：Menu:getId
     * @param rootCheck      判断E中为根节点的条件，如：x->x.getPId()==-1L , x->x.getParentId()==null,x->x.getParentMenuId()==0
     * @param setSubChildren E中设置下级数据方法，如： Menu::setSubMenus
     * @param <T>            ID字段类型
     * @param <E>            泛型实体对象
     * @return
     */
    public static <T, E> List<E> makeTree(List<E> menuList, Function<E, T> pId, Function<E, T> id, Predicate<E> rootCheck, BiConsumer<E, List<E>> setSubChildren) {
        // 按原数组顺序构建父级数据Map，使用Optional考虑pId为null
        Map<Optional<T>, List<E>> parentMenuMap = menuList.stream().collect(Collectors.groupingBy(
                node -> Optional.ofNullable(pId.apply(node)),
                LinkedHashMap::new,
                Collectors.toList()
        ));
        List<E> result = new ArrayList<>();
        for (E node : menuList) {
            // 添加到下级数据中
            setSubChildren.accept(node, parentMenuMap.get(Optional.ofNullable(id.apply(node))));
            // 如里是根节点，加入结构
            if (rootCheck.test(node)) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 树中过滤
     *
     * @param tree        需要过滤的树
     * @param predicate   过滤条件
     * @param getChildren 获取下级数据方法，如：MenuVo::getSubMenus
     * @param <E>         泛型实体对象
     * @return List<E> 过滤后的树
     */
    public static <E> List<E> filter(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getChildren) {
        return tree.stream().filter(item -> {
            if (predicate.test(item)) {
                List<E> children = getChildren.apply(item);
                if (children != null && !children.isEmpty()) {
                    filter(children, predicate, getChildren);
                }
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }


    /**
     * 树中搜索
     *
     * @param tree
     * @param predicate
     * @param getSubChildren
     * @param <E>
     * @return 返回搜索到的节点及其父级到根节点
     */
    public static <E> List<E> search(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getSubChildren) {
        Iterator<E> iterator = tree.iterator();
        while (iterator.hasNext()) {
            E item = iterator.next();
            List<E> childList = getSubChildren.apply(item);
            if (childList != null && !childList.isEmpty()) {
                search(childList, predicate, getSubChildren);
            }
            if (!predicate.test(item) && (childList == null || childList.isEmpty())) {
                iterator.remove();
            }
        }
        return tree;
    }
}
```



## websocket



### 后端

- 导入依赖

```plain
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
```

 

- 配置

```plain
package com.lty.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/ws")
                .setAllowedOrigins("*");
    }
}
```



- 处理器

```plain
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
```



注意websocket地址是ws://localhost:8088/ws     （后端地址+上下文路径+后缀）



### 前端

 处理自动重连、心跳保活、消息缓存等

```plain
<!DOCTYPE html>
<html>
<body>
  <input type="text" id="messageInput" placeholder="输入消息">
  <button onclick="sendMessage()">发送</button>
  <div id="messages"></div>
  
  <script>
    // 配置项
    const WS_CONFIG = {
      url: 'ws://localhost:8088/ws',
      reconnectInterval: 3000, // 重连间隔（毫秒）
      heartbeatInterval: 10000, // 心跳间隔（毫秒）
      maxReconnectAttempts: 10, // 最大重连次数
    };

    let socket = null;
    let reconnectAttempts = 0; // 重连次数计数
    let heartbeatTimer = null; // 心跳定时器
    let messageQueue = []; // 断连时的消息缓存队列

    // 初始化 WebSocket 连接
    function initWebSocket() {
      try {
        socket = new WebSocket(WS_CONFIG.url);
        
        // 连接成功
        socket.addEventListener('open', () => {
          logMessage('✅ 连接已建立');
          reconnectAttempts = 0; // 重置重连次数
          startHeartbeat(); // 启动心跳
          sendQueuedMessages(); // 发送缓存的消息
        });

        // 接收消息
        socket.addEventListener('message', (event) => {
          logMessage('📥 收到消息: ' + event.data);
        });

        // 连接关闭
        socket.addEventListener('close', () => {
          logMessage('❌ 连接已关闭');
          stopHeartbeat(); // 停止心跳
          reconnect(); // 触发重连
        });

        // 错误处理
        socket.addEventListener('error', (error) => {
          logMessage('⚠️ 连接错误: ' + (error.message || '未知错误'));
          socket.close(); // 错误时主动关闭，触发重连
        });
      } catch (e) {
        logMessage('💥 初始化失败: ' + e.message);
        reconnect();
      }
    }

    // 重连逻辑
    function reconnect() {
      if (reconnectAttempts >= WS_CONFIG.maxReconnectAttempts) {
        logMessage('🚫 达到最大重连次数，停止重连');
        return;
      }
      reconnectAttempts++;
      logMessage(`🔄 正在重连（第 ${reconnectAttempts} 次），${WS_CONFIG.reconnectInterval/1000} 秒后重试`);
      
      setTimeout(() => {
        initWebSocket();
      }, WS_CONFIG.reconnectInterval);
    }

    // 心跳保活（发送 ping，服务端需返回 pong 确认）
    function startHeartbeat() {
      stopHeartbeat(); // 先停止旧的定时器
      heartbeatTimer = setInterval(() => {
        if (socket.readyState === WebSocket.OPEN) {
          socket.send('ping'); // 发送心跳包（可自定义格式）
          // logMessage('📡 发送心跳包: ping');
        }
      }, WS_CONFIG.heartbeatInterval);
    }

    // 停止心跳
    function stopHeartbeat() {
      if (heartbeatTimer) {
        clearInterval(heartbeatTimer);
        heartbeatTimer = null;
      }
    }

    // 发送消息（含断连缓存）
    function sendMessage() {
      const message = document.getElementById('messageInput').value.trim();
      if (!message) {
        logMessage('📝 消息不能为空');
        return;
      }

      // 连接已打开，直接发送
      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(message);
        logMessage('📤 发送消息: ' + message);
        document.getElementById('messageInput').value = ''; // 清空输入框
      } else {
        // 连接未打开，加入缓存队列
        messageQueue.push(message);
        logMessage('📦 连接未建立，消息已缓存: ' + message);
        document.getElementById('messageInput').value = '';
      }
    }

    // 发送缓存的消息
    function sendQueuedMessages() {
      if (messageQueue.length === 0) return;
      logMessage(`📤 发送缓存的 ${messageQueue.length} 条消息`);
      
      messageQueue.forEach(msg => {
        socket.send(msg);
        logMessage('📤 发送缓存消息: ' + msg);
      });
      messageQueue = []; // 清空队列
    }

    // 日志输出
    function logMessage(message) {
      const messagesDiv = document.getElementById('messages');
      const p = document.createElement('p');
      p.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
      messagesDiv.appendChild(p);
      // 滚动到最新消息
      messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }

    // 页面卸载时关闭连接
    window.addEventListener('beforeunload', () => {
      stopHeartbeat();
      if (socket) {
        socket.close(1000, '页面关闭'); // 正常关闭
      }
    });

    // 初始化连接
    initWebSocket();
  </script>
</body>
</html>
```
