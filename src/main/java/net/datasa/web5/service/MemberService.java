package net.datasa.web5.service;

import org.hibernate.grammars.hql.HqlParser.IsEmptyPredicateContext;
import org.springframework.security.core.Transient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.domain.entity.MemberEntity;
import net.datasa.web5.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {
	// 회원정보 db저장
	private final MemberRepository repository;

	// 비밀번호 암호화
	private final BCryptPasswordEncoder passwoedEncoder;

	public void join(MemberDTO dto) {
		log.debug("전달된dto{}", dto);
		// dto 값을 읽어서 엔티티 생성

		MemberEntity entity = MemberEntity.builder().memberId(dto.getMemberId())
				// .memberPassword(dto.getMemberPassword()) 암호화 x
				.memberPassword(passwoedEncoder.encode(dto.getMemberPassword())).memberName(dto.getMemberName())
				.email(dto.getEmail()).phone(dto.getPhone()).address(dto.getAddress()).enabled(true)
				.rolename("ROLE_USER").build();// entity가 사용될떄 defult값은 대부분 사용 x

		log.debug("입력한entity{}", entity);
		repository.save(entity);

	}

	public boolean findId(String searchId) {
		log.debug("입력한 id:{}", searchId);
		boolean result = repository.existsById(searchId);
		log.debug("결과는?:{}", result);
		if (result == true) {
			return false;
		} else {
			return true;
		}

	}

	// 수정을 위해 db에서 정보를 가져오는 작업
	public MemberDTO getMember(String username) {
		// DB에서 아이디로 회원정보를 조회해서 Entity로 리턴 , 검색결과 없으면 예외
		MemberEntity entity = repository.findById(username)
				.orElseThrow(() -> new EntityNotFoundException(username + " : 아이디가 없습니다"));
		// 여러곳에서 필요할수 있으니 일단 모든 정보 추가함
		MemberDTO dto = MemberDTO.builder().memberId(entity.getMemberId()).memberName(entity.getMemberName())
				.memberPassword(entity.getMemberPassword()).email(entity.getEmail()).phone(entity.getPhone())
				.address(entity.getAddress()).enabled(entity.getEnabled()).rolename(entity.getRolename()).build();

		return dto;
	}

	// 개인정보 수정 처리
	public void update(MemberDTO dto) {
		// 조회를 해야함
		MemberEntity entity = repository.findById(dto.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("없는 ID"));
		// membeDTO의 비밀번호가 비어있으면 비번도 수정(비대입시 암호화)
		if (!dto.getMemberPassword().isEmpty()) {
			entity.setMemberPassword(passwoedEncoder.encode(dto.getMemberPassword()));
		}
		// 나머지 이름, 이메일, 전화, 주소는 무조건 대입
		entity.setMemberName(dto.getMemberName());
		entity.setEmail(dto.getEmail());
		entity.setPhone(dto.getPhone());
		entity.setAddress(dto.getAddress());
		// db에 저장(save 없지만 저장: 조회부분 + @Transactional)

	}

}
