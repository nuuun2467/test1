package net.datasa.web5.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor // 기본 생성자를 자동으로 생성
@AllArgsConstructor // 클래스의 모든 필드를 인자로 받는 생성자를 자동으로 생성
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "web5_board")
public class BoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가
	@Column(name = "board_num")
	private Integer boardNum;

	// 작성자 아이디
//	@Column(name = "member_id", length = 30)
//	private String memberId;

	// 작성자 정보

	// @ManyToOne //양쪽 테이블의 관계 만약 member테이블에서 본다면 @OneToMany
	@ManyToOne(fetch = FetchType.LAZY) // (fetch = FetchType.LAZY) 필요할때만 가져오게 하는역할
	@JoinColumn(name = "member_id", referencedColumnName = "member_id") // name 현재 테이블 컬럼명, referencedColumnName 상대편(변수
																		// 타입) 테이블
																		// 컬럼명
	private MemberEntity member; // board테이블의 member_id를 알면 member테이블의 해당 회원정보를 알수 있음

	@Column(name = "title", length = 1000, nullable = false)
	private String title;

	@Column(name = "contents", nullable = false, columnDefinition = "text")
	private String contents;

	@Column(name = "view_count", columnDefinition = "integer default 0")
	private Integer viewCount = 0; // 테이블에서 설정을 했지만 0이 안들어 갈수도 있으니 초기값 설정해두기

	@Column(name = "like_count", columnDefinition = "integer default 0")
	private Integer likeCount = 0;

	@Column(name = "original_name", length = 300)
	private String originalName;

	@Column(name = "file_name", length = 100)
	private String fileName;

	@CreatedDate // 작성시간@CreatedDate + @EntityListeners(AuditingEntityListener.class) +
					// 메인의@EnableJpaAuditing
	@Column(name = "create_date", columnDefinition = "timestamp default current_timestamp")
	private LocalDateTime createDate;

	@LastModifiedDate // 마지막 수정시간@LastModifiedDate + @EntityListeners(AuditingEntityListener.class) +
						// 메인의@EnableJpaAuditing 있어야 사용가능
	@Column(name = "update_date", columnDefinition = "timestamp default current_timestamp")
	private LocalDateTime updateDate;

	// array list
	// 글입장 보드1개 리프라이 여러개
	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true) // 값을넣어주는 코드 작성하는대신 자동으로 처리해줌
	private List<ReplyEntity> replyList;
}
