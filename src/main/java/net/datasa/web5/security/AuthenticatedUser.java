package net.datasa.web5.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
// 로그인할려고하는 사용자, 로그인 성공한 사용자의 인증정보 객제
public class AuthenticatedUser implements UserDetails {
	// 객체가 직렬화 될때 필요/다른서버에서 이용할때, 전달해줄 것
	private static final long serialVersionUID = -2757275378661085190L;
	// 인증관련 정보
	// Security는 변수를 읽는것이 아니고, 메서드를 읽음.
	String id; // 필수
	String password;// 필수
	String name;// 선택
	String roleName; // 권한 명 //필수
	boolean enabled; // 사용가능한 아이디인지 //필수

	@Override // 권한명 리턴
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(roleName));
	}

	@Override // 사용자의 비번 리턴
	public String getPassword() {
		return password;
	}

	@Override // 사용자 아이디 리턴
	public String getUsername() {
		return id;
	}

	@Override // true 인것만 접속가능
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override // true 인것만 접속가능
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override // true 인것만 접속가능
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override // 정상 아이디인지 여부
	public boolean isEnabled() {
		return enabled;
	}
}
