package net.datasa.web5.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원정보Entity
 */
@Builder // 빌더 패턴을 자동으로 생성
@Data
@NoArgsConstructor // 기본 생성자를 자동으로 생성
@AllArgsConstructor // 클래스의 모든 필드를 인자로 받는 생성자를 자동으로 생성
@Entity
@Table(name = "web5_member")
public class MemberEntity {
	@Id
	@Column(name = "member_id", length = 30)
	private String memberId;

	@Column(name = "member_password", nullable = false, length = 100)
	private String memberPassword;

	@Column(name = "member_name", nullable = false, length = 30)
	private String memberName;

	@Column(name = "email", length = 50)
	private String email;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "address", length = 200)
	private String address;

	@Column(name = "enabled", columnDefinition = "tinyint(1) default 1 check (enabled in(1,0)")
	private Boolean enabled;

	@Column(name = "rolename", length = 30, columnDefinition = "varchar(30) default 'ROLE_USER'check (rolename in('ROLE_USER','ROLE_ADMIN')")
	private String rolename;
}
