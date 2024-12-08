package com.bopcon.backend.config;

import com.bopcon.backend.config.jwt.TokenProvider;
import com.bopcon.backend.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;


@Configuration // 스프링 설정 파일임을 나타냄. 이 어노테이션 붙은 클래스를 자동으로 읽어 설정적용
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor // final 로 선언된 필드에 대해 생성자 자동 생성
public class WebSecurityConfig {
    private final TokenProvider tokenProvider;
    // TokenAuthenticationFilter 빈 생성
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider); // TokenProvider를 주입하여 필터 생성
    }

    private final UserDetailService userService;

    // 1. 스프링 시큐리티 기능 비활성화
    // WebSecurityCustomizer : 특정 요청을 시큐리티 필터 체인에서 제외.
    // 즉 H2 콘솔과 정적 리소스에 대한 요청은 스프링 시큐리티 필터를 거치지 않음
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console()) // h2 콘솔에 대한 요청을 필터링에서 제외
                .requestMatchers(new AntPathRequestMatcher("/static/**")) // 정적 리소스가 있는 경로 필터링에서 제외
                .requestMatchers(new AntPathRequestMatcher("/images/**")); // 정적 리소스가 있는 경로 필터링에서 제외
        // requestMatchers() : 특정 요청과 일치하는 url 에 대한 액세스를 설정
    }

    // 2. 특정 HTTP 요청에 대한 웹 기반 보안 구성
    // SecurityFilterChain : 스프링 시큐리티의 필터 체인 정의. 각 http 요청에 대해 어떻게 처리할지 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (SPA와 JWT 사용 시 비활성화가 일반적)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 인증 사용으로 세션 미사용
                .authorizeRequests(auth -> auth
                        // 인증 없이 접근 가능한 API
                        .requestMatchers(HttpMethod.GET, "/api/articles", "/api/articles/{id}", "/api/articles/artist/{id}").permitAll() // 글 조회는 모두 허용
                        .requestMatchers(HttpMethod.GET, "/api/comments/article/**").permitAll() // 댓글 목록 조회는 모두 허용
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/token").permitAll() // 회원가입, 로그인
                        .requestMatchers("/api/artists/**", "/api/new-concerts/**").permitAll() // 아티스트 및 콘서트 정보 조회
                        .requestMatchers("/api/search").permitAll() // 검색 API는 모두 허용
                        // 인증된 사용자만 접근 가능한 API
                        .requestMatchers(HttpMethod.POST, "/api/articles").authenticated() // 글 등록은 인증 필요
                        .requestMatchers("/api/comments/**").authenticated() // 댓글 추가, 삭제, 수정은 인증 필요
                        // 관리자 전용 API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 추가
                .build();
    }


    // 7. 인증 관리자 관련 설정
    // 사용자 정보를 가져올 서비스를 재정의 하거나, 인증 방법, 예를 들어 LDAP, JDBC 기반 인증 등을 설정할 때 사용
    // AuthenticationManager : 인증을 처리하는 주요 객체. 사용자의 인증 정보를 받아들이고, 이를 기반으로 인증이 성공실패 판단
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // 스프링 시큐리티에서 제공하는 기본 인증 제공자, 데이터베이스에서 사용자의 정보와 인증 처리
        authProvider.setUserDetailsService(userService); // 8. 사용자 정보 서비스 설정 : 인증에 사용할 UserDetailService 를 설정. 반드시 UserDetailsService 를 상속받은 클래스여야 함 (사용자 정보 가져오는 서비스)
        authProvider.setPasswordEncoder(bCryptPasswordEncoder); // 비밀번호를 암호화하고 비교하는 bCryptPasswordEncoder 를 설정
        return new ProviderManager(authProvider);
    }

    // 9. 패스워드 인코더로 사용할 빈 등록
    // 비밀번호를 암호화하는 데 사용. 해싱된 암호 값 저장
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}