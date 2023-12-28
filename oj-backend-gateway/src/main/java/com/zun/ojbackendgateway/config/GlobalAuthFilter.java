package com.zun.ojbackendgateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
		ServerHttpRequest serverHttpRequest = exchange.getRequest();
		String path = serverHttpRequest.getURI().getPath();
		//判断路径中是否包含inner,只允许内部调用
		if (antPathMatcher.match("/**/inner/**",path)){
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.FORBIDDEN);
			DataBufferFactory dataBufferFactory = response.bufferFactory();
			DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
			return response.writeWith(Mono.just(dataBuffer));
	    }
		//todo 统一权限校验，通过JWT获取登录用户信息，目前是Session存储登录态，这个方法里获取不到那个HttpServletRequest
		return chain.filter(exchange);
	}

	/**
	* 将本过滤器的优先级提到最高
	* @return
	*/
	@Override
	public int getOrder() {
		return 0;
	}
}