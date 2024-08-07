package net.datasa.web5.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리플 정보 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
	// 리플 번호, 본문 글번호, 작성자 아이디, 작성자이름, 리플내용, 작성시간
	private Integer replyNum; // 리플번호
	private Integer boardNum; // 본문 글번호
	private String memberId; // 작성자 아이디
	private String memberName; // 작성자 이름
	private String contents; // 리플 내용
	private LocalDateTime createDate; // 작성시간
}
