package net.datasa.web5.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.web5.domain.entity.BoardEntity;

/**
 * 회원정보DTO
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
	private String memberId;
	private String memberPassword;
	private String memberName;
	private String email;
	private String phone;
	private String address;
	private Boolean enabled; // 계정상태
	private String rolename; // 권한 명

}
