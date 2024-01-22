package com.zun.openapigateway.filter;

import cn.hutool.core.util.StrUtil;
import com.zun.ojapiclientsdk.util.SignUtils;
import com.zun.ojbackendmodel.model.dto.interfaceinfo.InvokeCountRequest;
import com.zun.ojbackendmodel.model.entity.InterfaceInfo;
import com.zun.ojbackendmodel.model.entity.User;
import com.zun.ojbackendmodel.model.entity.UserInterfaceInfo;
import com.zun.ojbackendmodel.model.enums.UserInterfaceStatusEnum;
import com.zun.ojbackendserviceclient.service.InterfaceFeignClient;
import com.zun.ojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * api网关全局过滤器
 *
 * @author ZunF
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private InterfaceFeignClient interfaceService;

    @Resource
    private UserFeignClient userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String url = request.getURI().toString();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + url);
        log.info("请求方式：" + method);
        log.info("请求参数：" + request.getQueryParams());
        log.info("请求来源地址：" + request.getRemoteAddress());
        // 2.判断用户是否被禁止调用此接口.若数据库中没有数据就插入一条数据
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        InterfaceInfo interfaceInfo = interfaceService.getByUrlAndMethod(url, method);
        if (interfaceInfo == null) {
            return handleInvokeError(response);
        }
        //判断是否存在此调用关系
        UserInterfaceInfo userInterfaceInfo = interfaceService.getUserInterfaceInfo(accessKey, interfaceInfo.getId());
        if (userInterfaceInfo == null) {
            userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setAccessKey(accessKey);
            userInterfaceInfo.setInterfaceId(interfaceInfo.getId());
            userInterfaceInfo.setLeftNum(50); //初始赠送50次
            interfaceService.saveUserInterfaceInfo(userInterfaceInfo);
        } else {
            if (!userInterfaceInfo.getStatus().equals(UserInterfaceStatusEnum.NORMAL.getValue())) {
                return handleNoAuth(response);
            }
        }
        // 3.用户鉴权（判断ak、sk是否合法、防止重放攻击）
        User user = userService.getByAccessKey(accessKey);
        String secretKey = user.getSecretKey();
        String body = headers.getFirst("body");
        String sign = headers.getFirst("sign");
        if (!StrUtil.equals(sign, SignUtils.genSign(body, secretKey))) {
            return handleNoAuth(response);
        }
        String nonce = headers.getFirst("nonce");
        // todo 防止重放、校验随机数nonce
        //防止重放、校验时间戳是否在规定范围内
        long timestamp = Long.parseLong(headers.getFirst("timestamp"));
        long curTimestamp = System.currentTimeMillis() / 1000;
        long FIVE_MINUTE = 5 * 60;//上面三个单位都是秒s
        if (curTimestamp - timestamp > FIVE_MINUTE) {
            return handleNoAuth(response);
        }
        // 4.请求转发，调用模拟接口
        InvokeCountRequest invokeCountRequest = new InvokeCountRequest();
        invokeCountRequest.setAccessKey(accessKey);
        invokeCountRequest.setInterfaceId(interfaceInfo.getId());
        return handleResponse(exchange, chain, invokeCountRequest);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, InvokeCountRequest invokeCountRequest) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                //装饰着模式，增强功能
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 6.调用成功，接口调用次数+1
                                // WebFlux异步调用，同步会报错
                                CompletableFuture.runAsync(() -> {
                                    interfaceService.invokeCount(invokeCountRequest);
                                });
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                // 5.响应日志
                                log.info("响应结果：" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }


    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}