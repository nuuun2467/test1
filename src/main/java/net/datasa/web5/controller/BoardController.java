package net.datasa.web5.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.BoardDTO;
import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.domain.dto.ReplyDTO;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.service.BoardService;
import net.datasa.web5.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {
	private final BoardService service;
	@Value("${board.pageSize}")
	int pageSize; // 페이지당 글 수

	@Value("${board.linkSize}")
	int linkSize; // 페이지이동 링크 수

	@Value("${board.uploadPath}")
	String uploadPath; // 첨부파일 저장 경로

	// 링크처리 메서드

	@GetMapping("list") // String a = null;(메인화면에서 들어왔을떄), String b=""(검색버튼 눌렀지만 입력않했을때);
	public String list(Model m, @RequestParam(name = "page", defaultValue = "1") int page, // 몇페이지 볼지
			@RequestParam(name = "searchType", defaultValue = "") String searchType, // 어떤걸 대상으로
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord) { // 검색할 단어
		log.debug("properties 값 pageSize값={}, linkSize 값={}, upload값={}", pageSize, linkSize, uploadPath);// 100개중
																											// 10페이지면 총
																											// 10페이지 있음
		log.debug("요청파라미터 : page={}, searchType={},searchWord={}", page, searchType, searchWord);
		// 게시글 테이블의 정보를 조회해서 모델에 저장하고 list.html로 포워딩 ///Page 객체 //page단위로 잘라서 주는건 자동
		// 현재페이지, 페이지당 글 수, 검색대상, 검색어
		Page<BoardDTO> boardPage = service.getList(page, pageSize, searchType, searchWord);
		// 메서드 테스트

		log.debug(" 목록정보 getContent() : {}", boardPage.getContent());// 글 10개 ->리스트
		log.debug(" 현재페이지 getNumber() : {}", boardPage.getNumber());
		log.debug(" 전체 개수 getTotalElements() : {}", boardPage.getTotalElements()); // TotalElements -()
		log.debug(" 전체 페이지수 getTotalPages() : {}", boardPage.getTotalPages()); // 글이 100개인데 10페이지면 총 10개
		log.debug(" 한 페이지당 글 수 getSize() : {}", boardPage.getSize());
		log.debug(" 이전페이지 존재 여부 hasPrevious() : {}", boardPage.hasPrevious());
		log.debug(" 다음페이지 존재 여부 boardPage.hasNext() : {}", boardPage.hasNext());
		m.addAttribute("boardPage", boardPage); // 출력할 글정보
		m.addAttribute("page", page);// 현재페이지
		m.addAttribute("linkSize", linkSize);// 페이지이동링크수
		m.addAttribute("searchType", searchType);// 검색기분
		m.addAttribute("searchWord", searchWord);// 검색어
		return "boardView/list";
	}

	// 링크처리 메서드
	@GetMapping("write")
	public String write() {
		return "boardView/write";
	}

	// 게시판 입력값 저장 메서드
	@PostMapping("write")
	public String input(@AuthenticationPrincipal AuthenticatedUser user, @ModelAttribute BoardDTO dto,
			@RequestParam("upload") MultipartFile upload) {
		dto.setMemberId(user.getUsername());
		// 업로드한 파일에 대한 정보 확인
		if (upload != null) {
			log.debug("파일 존재 여부 : {}", upload.isEmpty()); // true면 객체는 있지만 파일은 없음
			log.debug("파라미터 이름 : {}", upload.getName());
			log.debug("파일의 이름 : {}", upload.getOriginalFilename());
			log.debug("크기 : {}", upload.getSize());
			log.debug("파일 종류 : {}", upload.getContentType());
		}
		service.save(dto, uploadPath, upload);// db에 저장된건 단지 파일의 이름일 뿐임. 즉 삭제는 따로 처리해줘야 함
		return "redirect:list";
	}

	// 상세페이지 이동
	@GetMapping("read")
	public String readBoard(@RequestParam("boardNum") Integer boardNum, Model model) {

		try {
			BoardDTO read = service.read(boardNum);
			model.addAttribute("read", read);
			return "boardView/read";
		} catch (Exception e) {
			e.printStackTrace(); // 메세지 출력
			return "redirect:list"; // 종류상관없이 모든 예외 발생시 글목록으로 이동
			// 이런식으로 없는 글 번호 입력해서 이동하면 글목록으로 http://localhost:8888/board/read?boardNum=22222
		}

	}

	// 삭제처리
	@GetMapping("delete/{boardNum}")
	public String listDelete(@PathVariable("boardNum") Integer boardNum,
			@AuthenticationPrincipal AuthenticatedUser user) {
		try {
			service.delete(boardNum, user.getUsername(), uploadPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/board/list";

	}

	// 수정폼 이동
	@GetMapping("update")
	public String listeUpdate(@RequestParam("boardNum") Integer boardNum,
			@AuthenticationPrincipal AuthenticatedUser user, Model m) {

		try {
			BoardDTO boardDTO = service.read(boardNum);
			if (!user.getUsername().equals(boardDTO.getMemberId())) {
				throw new RuntimeException("수정 권한이 없습니다.");
			}
			m.addAttribute("board", boardDTO);
			return "boardView/updateForm";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:list";
		}
	}

	// 수정폼에서 수정 처리
	@PostMapping("update")
	public String update(@ModelAttribute BoardDTO boardDTO, @AuthenticationPrincipal AuthenticatedUser user,
			@RequestParam("upload") MultipartFile upload) {
		try {
			service.listUpdate(boardDTO, user, uploadPath, upload);
			return "redirect:read?boardNum=" + boardDTO.getBoardNum();
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:list";
		}
	}

	// 리플 쓰기
	@PostMapping("replyWrite")
	public String replyWrite(@ModelAttribute ReplyDTO replyDTO, @AuthenticationPrincipal AuthenticatedUser user) {
		replyDTO.setMemberId(user.getUsername());
		service.replyWrite(replyDTO);
		return "redirect:read?boardNum=" + replyDTO.getBoardNum();
	}

	// 리플 삭제하기
	@GetMapping("replyDelete")
	public String replyDelete(@ModelAttribute ReplyDTO replyDTO, @AuthenticationPrincipal AuthenticatedUser user) {
		try {
			service.replyDelete(replyDTO.getReplyNum(), user.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:read?boardNum=" + replyDTO.getBoardNum();
	}

	// 파일 다운로드
	// 리턴하는 방식으로 파일을 보내지 않음 => void
	// 다운로드 요청이 오면 , HttpServletResponse response 에 요청한 상대방에 대한
	// 정보가 담겨있음 (파일다운로드 누르면 누른 그 상대방에 대한 정보)
	@GetMapping("download")
	public void download(@RequestParam("boardNum") Integer boardNum, HttpServletResponse response) {
		service.download(boardNum, response, uploadPath);
	}

}
