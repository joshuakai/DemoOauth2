package com.example.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    // 該物件用來支援 password 模式
    @Autowired
    AuthenticationManager authenticationManager;

    // 該物件用來將令牌資訊儲存到記憶體中
    @Autowired(required = false)
    TokenStore inMemoryTokenStore;

    // 該物件將為重新整理token提供支援
    @Autowired
    UserDetailsService userDetailsService;

    // 指定密碼的加密方式
    @Bean
    PasswordEncoder passwordEncoder() {
        // 使用BCrypt強雜湊函式加密方案（金鑰迭代次數預設為10）
        return new BCryptPasswordEncoder();
    }

    // 配置 password 授權模式
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.inMemory()
                .withClient("password")
                .authorizedGrantTypes("password", "refresh_token") //授權模式為password和refresh_token兩種
                .accessTokenValiditySeconds(1800) // 配置access_token的過期時間
                .resourceIds("rid") //配置資源id
                .scopes("all")
                .secret("$2a$10$RMuFXGQ5AtH4wOvkUqyvuecpqUSeoxZYqilXzbz50dceRsga.WYiq"); //123加密後的密碼
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(inMemoryTokenStore) //配置令牌的儲存（這裡存放在記憶體中）
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 表示支援 client_id 和 client_secret 做登入認證
        security.allowFormAuthenticationForClients();
    }
}