package net.datasa.web5.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 리플 엔티티
 */
@Builder
@Data
@ToString(exclude = "board") // 여기서는 board 출력x(아니면 무한 참조)
@NoArgsConstructor // 기본 생성자를 자동으로 생성
@AllArgsConstructor // 클래스의 모든 필드를 인자로 받는 생성자를 자동으로 생성
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "web5_reply")
public class ReplyEntity {
	//
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가
	@Column(name = "reply_num")
	private Integer replyNum;
	// 게시글 정보 (외래키로 참조)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_num", referencedColumnName = "board_num")
	private BoardEntity board;
	// 작성자 정보 (외래키로 참조)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private MemberEntity member; // 가져올때 회원정보를 가져옴
	// 리플 내용
	@Column(name = "contents", length = 21000, nullable = false)
	private String contents;
	// 작성시간
	@CreatedDate
	@Column(name = "create_date", columnDefinition = "timestamp default current_timestamp")
	LocalDateTime createDate;

}
