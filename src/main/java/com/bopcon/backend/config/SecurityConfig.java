package com.bopcon.backend.config;

import com.bopcon.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 PasswordEncoder 설정
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) // h2 콘솔에 대한 요청을 필터링에서 제외
                .requestMatchers(new AntPathRequestMatcher("/static/**"))
                .requestMatchers(new AntPathRequestMatcher("/images/**")); // 정적 리소스가 있는 경로 필터링에서 제외
        // requestMatchers() : 특정 요청과 일치하는 url 에 대한 액세스를 설정
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/signup", "/auth/login", "/h2-console/**", "/").permitAll() // 회원가입, 로그인은 인증 필요 없음
//                        .requestMatchers("/api/artists/**", "/api/new-concerts/**").permitAll() // 누구나 접근 가능하도록 설정
//                        .anyRequest().authenticated() // 그 외의 요청은 인증이 필요함
//                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*", "/auth/signup", "/auth/login").permitAll()
                        .requestMatchers("/api/artists/**", "/api/new-concerts/**").permitAll()
                        //.anyRequest().permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
