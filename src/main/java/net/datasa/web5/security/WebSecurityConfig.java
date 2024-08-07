package net.datasa.web5.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 환경설정을 담당하자는 자바 클래스에 붙이는 어노테이션
@EnableWebSecurity // security를 사용 가능하게하는 어노테이션
public class WebSecurityConfig {
	// 로그인 없이 접근 가능 경로
	private static final String[] PUBLIC_URLS = { "/" // 메인화면
			, "/member/loginForm", "/member/joinForm", "/member/idCheck", "board/list", "/css/**", "/js/**", "/images/" // 로그인
																														// 없이
			// 접근할 수 있는
			// 페이지
			// 경로(url)
			// //"/경로/**"경로 아래의 모든 파일
			// 허용
	};

	@Bean
	protected SecurityFilterChain config(HttpSecurity http) throws Exception {
		http
				// 요청에 대한 권한 설정
				.authorizeHttpRequests(author -> author.requestMatchers(PUBLIC_URLS).permitAll() // 모두 접근 허용
						.anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
				)
				// HTTP Basic 인증을 사용하도록 설정
				.httpBasic(Customizer.withDefaults())
				// 폼 로그인 설정
				.formLogin(formLogin -> formLogin.loginPage("/member/loginForm") // 로그인폼 페이지 경로 // 로그인 안했을시 로그인 폼으로 이동시킴
																					// //사용자가 로그인하러 눌렀을때, 로그인하지않았을때도
																					// 시큐리티가 이쪽으로 보냄
						.usernameParameter("id") // 폼의 ID 파라미터 이름 //동일하게 설정
						.passwordParameter("password") // 폼의 비밀번호 파라미터 이름 //동일하게 설정
						.loginProcessingUrl("/member/login") // 로그인폼 제출하여 처리할 경로 //action을 여기랑 맞추기, method는 post임
						.defaultSuccessUrl("/") // 로그인 성공 시 이동할 경로
						.permitAll() // 로그인 페이지는 모두 접근 허용
				)
				// 로그아웃 설정
				.logout(logout -> logout.logoutUrl("/member/logout") // 로그아웃 처리 경로
						.logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 경로
				);

		http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

	// 비밀번호 암호화를 위한 인코더를 빈으로 등록
	@Bean // BCryptPasswordEncoder는
	// 내가 만든 파일이 아님
	// = 수정불가능
	// = 어노테이션 추가 불가능
	// = 빈으로 등록 불가능
	// = 객체 생성하여 빈으로 등록
	// = 메서드에 @bean사용
	// 예를 들어 내가만든서비스는 @Service 를 사용하여 bean에 등록 할수 있었음
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
