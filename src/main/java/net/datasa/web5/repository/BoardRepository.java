package net.datasa.web5.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.web5.domain.entity.BoardEntity;

//findbyId, deletebyId 사용할떄 사용
@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> { // 추천기능, 이웃기능은 복합기를 사용
	// find로 시작하 메서드는 select명령 생성 findBy컬럼명 =>findByTitle : 정확히 일치하는 것만 찾음,
	// like,와일드카드 => Containing추가
	// member.memberid 이런경우 메서드 이름 조심
	// 추상메서드(구현해야함) -> @Repository

	// 조건1)
	List<BoardEntity> findByTitleContaining(String s, Sort sort);

	// 조건2) 제목, 본문에 있는지 단 소트사용 하지 않고
	List<BoardEntity> findByTitleContainingOrContentsContainingOrderByTitleDesc(String s, String c);

	// 수정후 조건1)
	// 전달된 문자열을 제목에서 검색한 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByTitleContaining(String s, Pageable p);

	// 전달된 문자열을 내용에서 검색한 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByContentsContaining(String s, Pageable p);

	// 전달된 문자열을 작성자 아이디에서 검색한 후 지정한 한페이지 분량 리턴
	Page<BoardEntity> findByMember_MemberId(String s, Pageable p);
	// 쿼리를 적어서 파라미터 받아서 사용 가능
}
