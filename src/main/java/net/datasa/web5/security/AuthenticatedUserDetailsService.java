package net.datasa.web5.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.entity.MemberEntity;
import net.datasa.web5.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {// security가 아이디와 비번을 받아서
																								// 아이디만
																								// AuthenticatedUserDetailsService에
																								// 넘겨줌
		// 전달받은 사용자 아이디(username)으로 DB에서 사용자 번호 조회
		// 1.아이디가 없으면 예외(있으면 entity객체생성하여 정보담음)
		MemberEntity entity = memberRepository.findById(username)
				.orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다"));
		// 2.entity에 담긴 정보를 UserDetails 객체 생성해서담아주고, security에 리턴
		// (시큐리티는 그냥 주면 못알아들음.UserDetails을 상속한 객체로 줘야함)
		// 객체생성후 db에서 요구한 정보 넣어주기(여기선 로그인에 필요한 정보만)
		AuthenticatedUser user = AuthenticatedUser.builder().id(username).password(entity.getMemberPassword())
				.name(entity.getMemberName()).roleName(entity.getRolename()).enabled(entity.getEnabled()).build();
		log.debug("인증정보:{}", user);
		return user; // 시큐리티가 받아서 확인하고 다 정상이면 정보를 세션에 저장하고 로그인 시켜줌
	}

}
