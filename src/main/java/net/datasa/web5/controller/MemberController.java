package net.datasa.web5.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.service.MemberService;

/**
 * 회원정보관련 콘트롤러
 */
@Slf4j
@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService service;

	// 회원가입페이지 링크
	@GetMapping("joinForm")
	public String joinForm() {
		return "memberView/joinForm";
	}

	// 회원가입 처리
	@PostMapping("joinForm")
	public String input(@ModelAttribute MemberDTO member) {
		log.debug("전달된member{}", member);
		service.join(member);
		return "redirect:/";
	}

	/**
	 * 회원가입페이지에서 ID중복확인 버튼을 클릭하면 새창으로 보여줄 검색 페이지로 이동
	 * 
	 * @return id검색 html파일로 이동
	 */
	@GetMapping("idCheck")
	public String idCheck() {
		return "memberView/idCheck";
	}

	/**
	 * id중복 확인 페이지에서 검색 요청을 했을떄 처리
	 * 
	 * @param searchId 검색할 아이디
	 * @return id검색 html파일 경로
	 */
	@PostMapping("idCheck")
	public String Check(@RequestParam("searchId") String searchId, Model m) {
		log.debug("전달된searchId:{}", searchId);
		boolean result = service.findId(searchId);
		log.debug("컨트롤러 result:{}", result);

		m.addAttribute("searchId", searchId);
		m.addAttribute("result", result);

		return "memberView/idCheck";
	}

	/**
	 * 로그인
	 */
	@GetMapping("loginForm")
	public String loginForm() {
		return "memberView/loginForm";
	}

	/**
	 * 개인정보 수정 폼으로 이동
	 * 
	 * @param user로 로그인한 사용자 정보
	 * @return 수정폼 HTML 경로
	 */
	// 이 코드는 회원이 로그인할때 사용한 정보만 사용하는거라 (최소한의 정보만 있음 이메일, 휴대폰 번호는 없음)불가능 =>db까지 갔다가
	// 와야함
//	@GetMapping("info")
//	public String info(@AuthenticationPrincipal AuthenticatedUser user, Model m) {
//
//		// 서비스로 아이디를 전달하여 사용자 정보 조회(MemberDTO 타입으로 리턴)
//		// 조회한 회원정보를 모델에 저장하고 html로 포워딩
//		log.debug("로그인한 아이디: {}", user.getId());
//		log.debug("로그인한 아이디: {}", user);
//		m.addAttribute("user", user);
//		return "memberView/info";
//	}
	@GetMapping("info")
	public String info(@AuthenticationPrincipal AuthenticatedUser user, Model m) {

		// 서비스로 아이디를 전달하여 사용자 정보 조회(MemberDTO 타입으로 리턴)
		MemberDTO dto = service.getMember(user.getUsername());
		// 조회한 회원정보를 모델에 저장하고 html로 포워딩
		m.addAttribute("dto", dto);
		return "memberView/info";
	}

	/**
	 * 개인정보 수정 처리
	 * 
	 * @param user      로그인 정보
	 * @param memberDTO 수정폼에서 입력한 값들
	 * @return
	 */
	// 이 코드는 ID가 없어서 어떤 정보를 수정할지 알수 없음
//	@PostMapping("info")
//	public String info2(@ModelAttribute MemberDTO dto, Model m) {
//		service.update(dto);
//		return "redirect:/";
//	}
	@PostMapping("info")
	public String info2(@AuthenticationPrincipal AuthenticatedUser user, @ModelAttribute MemberDTO dto) {
		dto.setMemberId(user.getUsername());// 수정할때 id값이 없으므로 로그인때 받은 정보로 id입력

		// 서비스로 전달하여 DB수정
		service.update(dto);
		return "redirect:/";
	}

}
