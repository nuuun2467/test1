package net.datasa.web5.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
	private Integer boardNum;
	private String memberId;
	private String memberName;
	private String title;
	private String contents;
	private Integer viewCount;
	private Integer likeCount;
	private String originalName;
	private String fileName;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	// Reply정보 담을 변수 추가
	private List<ReplyDTO> replyList; // 리플이 없다면 리스트는 초기화되어 있지만 그 안에는 어떤 요소도 없는 상태 즉 null도 아니고, dto(모든변수가 nulll인)가
										// 있는것도 아니고, 요소가 0개임
}
