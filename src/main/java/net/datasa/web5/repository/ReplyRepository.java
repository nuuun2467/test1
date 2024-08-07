package net.datasa.web5.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.web5.domain.entity.BoardEntity;
import net.datasa.web5.domain.entity.ReplyEntity;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
	// 글번호로 리플목록 조회. 라플번호 순으로 정렬

	// select * from web5_reply where board_num = 1 order by reply_num;
	List<ReplyEntity> findByBoard_BoardNumOrderByReplyNum(Integer n);
}
