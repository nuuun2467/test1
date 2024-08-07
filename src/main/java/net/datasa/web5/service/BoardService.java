package net.datasa.web5.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.core.model.Model;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.BoardDTO;
import net.datasa.web5.domain.dto.ReplyDTO;
import net.datasa.web5.domain.entity.BoardEntity;
import net.datasa.web5.domain.entity.MemberEntity;
import net.datasa.web5.domain.entity.ReplyEntity;
import net.datasa.web5.repository.BoardRepository;
import net.datasa.web5.repository.MemberRepository;
import net.datasa.web5.repository.ReplyRepository;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.util.FileManager;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BoardService {
	private final BoardRepository boardrepository;
	private final MemberRepository memberrepository;
	private final ReplyRepository replyrepository;
	private final FileManager fileMabager;

	/**
	 * 
	 * @param dto        저장할 글 정보
	 * @param uploadPath 파일을 저장할 경로
	 * @param upload     업로드된 파일 정보
	 */
	public void save(BoardDTO dto, String uploadPath, MultipartFile upload) {
		// MultipartFile upload가 null이거나 enmpty면 저장할 필요 없음

		// 빌더를 사용하면 entity 설정의 = 0 이 적용 x
//		BoardEntity entity = BoardEntity.builder().boardNum(dto.getBoardNum()).memberId(dto.getMemberId())
//				.title(dto.getTitle()).contents(dto.getContents()).originalName(dto.getOriginalName())
//				.fileName(dto.getFileName()).createDate(dto.getCreateDate()).updateDate(dto.getUpdateDate()).build();

		// 빌더를 사용하면 entity 설정의 = 0 이 적용 x
//		BoardEntity entity = BoardEntity.builder().boardNum(dto.getBoardNum()).memberId(dto.getMemberId())
//				.title(dto.getTitle()).contents(dto.getContents()).viewCount(dto.getViewCount())
//				.likeCount(dto.getLikeCount()).originalName(dto.getOriginalName()).fileName(dto.getFileName())
//				.createDate(dto.getCreateDdte()).updateDate(dto.getUpdateDate()).build();
//		
		// 빌더를 사용할때 직접 0으로 넣어주기
//		BoardEntity entity = BoardEntity.builder().boardNum(dto.getBoardNum()).memberId(dto.getMemberId())
//				.title(dto.getTitle()).contents(dto.getContents()).viewCount(0).likeCount(0)
//				.originalName(dto.getOriginalName()).fileName(dto.getFileName()).createDate(dto.getCreateDate())
//				.updateDate(dto.getUpdateDate()).build();

		// 생성자, get,set이용은 문제 없음
		// 글 작성자 정보 조회
		MemberEntity memberEntity = memberrepository.findById(dto.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다"));

		BoardEntity entity = new BoardEntity();

		// entity.setMemberId(dto.getMemberId());
		entity.setMember(memberEntity);
		entity.setTitle(dto.getTitle());
		entity.setContents(dto.getContents());

		// db에 저장된건 단지 파일의 이름일 뿐임. 즉 삭제는 따로 처리해줘야 함
		// 첨부파일 있으면 처리
		try {
			if (upload != null && !upload.isEmpty()) {// 업로드한 파일이 있을때,
				String fileName = fileMabager.saveFile(uploadPath, upload);
				entity.setOriginalName(upload.getOriginalFilename());
				entity.setFileName(fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("저장되는 엔티티: {}", entity);
		boardrepository.save(entity);

	}

	/**
	 * @param page       현재 페이지
	 * @param pageSize   한 페이지당 글 수
	 * @param searchType 검색 대상(제목:title, 본문:contents, 작성자ㅣid)
	 * @param searchWord 검색어
	 * @return 글 정보가 한페이지 분량 저장된 page객체
	 * 
	 */
//	// 게시글 목록을 읽어서 전달해주는 메서드
//	public Page<BoardDTO> getList(int page, int pageSize, String searchType, String searchWord) {
//
//		Pageable p = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "boardNum"); // 정렬 필수 page 가 0이 제일 최근 글,
//																							// 사용자 입장 -1 //Pageable는 정보를
//																							// 페이지 단위로 가져온더ㅏ
//																							// 전달해주즌 Page는 전달된 정보로 페이지를
//																							// 나누는
//
//		Page<BoardEntity> boardEntityPage = brepository.findAll(p);
//		// 리턴받은 BoardEntityList의 정보를 바탕으로 BoardDTO가 저장된 page객체를 생성해서 리턴
//		Page<BoardDTO> dtoPage = boardEntityPage.map(this::convertToDTO); // map반목문 역할, 람다 표현식 this는 entity객체 하나하나를 그 순간
//																			// 가리키고 있음, this를 통해 함수 호출
//		return dtoPage;
//
//	}

	public Page<BoardDTO> getList(int page, int pageSize, String searchType, String searchWord) {

		Pageable p = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "boardNum"); // 정렬 필수 page 가 0이 제일 최근 글,
																							// 사용자 입장 -1 //Pageable는 정보를
																							// 페이지 단위로 가져온더ㅏ
																							// 전달해주즌 Page는 전달된 정보로 페이지를
																							// 나누는

		// Page<BoardEntity> boardEntityPage = brepository.findAll(p);
		Page<BoardEntity> boardEntityPage;
		switch (searchType) {
		case "title":
			boardEntityPage = boardrepository.findByTitleContaining(searchWord, p);
			break;
		case "contents":
			boardEntityPage = boardrepository.findByContentsContaining(searchWord, p);
			break;
		case "id":
			boardEntityPage = boardrepository.findByMember_MemberId(searchWord, p);
			break;
		default:
			boardEntityPage = boardrepository.findAll(p);
			break;
		}

		// Page<BoardEntity> boardEntityPage =
		// brepository.findByContentsContaining(searchWord, p);
		// Page<BoardEntity> boardEntityPage =
		// brepository.findByMember_MemberId(searchWord, p);
		// 리턴받은 BoardEntityList의 정보를 바탕으로 BoardDTO가 저장된 page객체를 생성해서 리턴
		Page<BoardDTO> dtoPage = boardEntityPage.map(this::convertToDTO); // map반목문 역할, 람다 표현식 this는 entity객체 하나하나를 그 순간
																			// 가리키고 있음, this를 통해 함수 호출
		return dtoPage;

	}

	/**
	 * BoardEntity 객체를 전달받아 BoardDTO 객체로 변환하여 리턴
	 * 
	 * @Param Entity DB에서 읽은 객체를 담은 엔티티 객체
	 * @Return 출력용 정보를 담은 DTO 객체
	 */
	// 글 목록 읽을때, 글내용 읽을떄 상요
	private BoardDTO convertToDTO(BoardEntity entity) {
		return BoardDTO.builder().boardNum(entity.getBoardNum()).memberId(entity.getMember().getMemberId())
				.memberName(entity.getMember().getMemberName()).title(entity.getTitle()).contents(entity.getContents())
				.viewCount(entity.getViewCount()).likeCount(entity.getLikeCount())
				.originalName(entity.getOriginalName()).fileName(entity.getFileName())
				.createDate(entity.getCreateDate()).updateDate(entity.getUpdateDate()).build();
	}

	public BoardDTO read(Integer boardNum) {
		// 글 번호로 BoardEntity 조회 없으면 예외
		// 있으면 BoardDTO로 변환하여 리턴
		BoardEntity entity = boardrepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다"));
		entity.setViewCount(entity.getViewCount() + 1); // ENTITY에 설정

		log.debug("조회된 게시글 정보 : {}", entity); // 무한반복 처리

		// 있으면 boardDTO로 변환
		BoardDTO dto = convertToDTO(entity);

		// 리플 목록을 DTO로 변환하여 추가
		List<ReplyDTO> replyList = new ArrayList<>();
		for (ReplyEntity replyEntity : entity.getReplyList()) {
			ReplyDTO replyDTO = ReplyDTO.builder().replyNum(replyEntity.getReplyNum())
					.boardNum(replyEntity.getBoard().getBoardNum()).memberId(replyEntity.getMember().getMemberId())
					.memberName(replyEntity.getMember().getMemberName()).contents(replyEntity.getContents())
					.createDate(replyEntity.getCreateDate()).build();
			replyList.add(replyDTO);
		}
		dto.setReplyList(replyList);

		return dto;
	}

	/**
	 * 
	 * @param boardNum   삭제할 글번호
	 * @param username   로그인한 아이디
	 * @param uploadPath 첨부파일이 저장된 경로
	 */
	public void delete(Integer boardNum, String username, String uploadPath) {
		// 삭제 버튼이 아닌 주소창에 입력해서 글을 삭제할 위험이 있음. 본안확인 필수
		BoardEntity entity = boardrepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다"));
		// 본인확인 필수 (url창 조작할 가능성)
		if (!entity.getMember().getMemberId().equals(username)) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}
		// isempty 는 항상 null체크도 써주자
		// 첨부파일 삭제
		if (entity.getFileName() != null && !entity.getFileName().isEmpty()) {
			try {
				boolean result = fileMabager.deleteFile(uploadPath, entity.getFileName());
				if (result == true) {
					log.debug("삭제완료");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boardrepository.deleteById(boardNum);// .deleteById(id); 해당하는 한 행을 지워줌 리턴값 없음

	}

	public void listUpdate(BoardDTO dto, AuthenticatedUser user, String uploadPath, MultipartFile upload) {
		BoardEntity entity = boardrepository.findById(dto.getBoardNum())
				.orElseThrow(() -> new EntityNotFoundException("게시글이  없습니다"));

		if (!entity.getMember().getMemberId().equals(user.getUsername())) {
			throw new RuntimeException("수정 권한이 없습니다.");
		}

		// 첨부파일 처리
		// 수정하면서 새로 첨부한 파일이 있으면
		if (upload != null && !upload.isEmpty()) {
			// 그 전에 업로드한 기존 파일이 있으면 먼저 파일 삭제
			if (entity.getFileName() != null && !entity.getFileName().isEmpty()) {
				File file = new File(uploadPath, entity.getFileName()); // 실제 하드디스크에 존재하는 파일 가르키고 있음
				file.delete();// 하드디스크에서 파일을 삭제하는 메서드. 리턴타입 true/false
				log.debug("삭제완료");
			}
			// 새로운 파일의 이름을 바꿔서 복사
			File directoryPath = new File(uploadPath);
			if (!directoryPath.isDirectory()) {
				directoryPath.mkdirs();
			}
			String originalName = upload.getOriginalFilename();
			String extenxion = originalName.substring(originalName.lastIndexOf("."));// 확장자
			String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));// 날자를 문자열로
			String uuidString = UUID.randomUUID().toString();
			String fileName = dateString + "_" + uuidString + extenxion;

			try {
				File filePath = new File(uploadPath + "/" + fileName);
				// 엔티티에 새 파일의 원래이름, 저장된 이름 추가
				upload.transferTo(filePath);
				entity.setOriginalName(originalName);
				entity.setFileName(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		entity.setTitle(dto.getTitle());
		entity.setContents(dto.getContents());
	}

	public void replyWrite(ReplyDTO replyDTO) {
		MemberEntity memberEntity = memberrepository.findById(replyDTO.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("사용자 아이디가 없습니다."));

		BoardEntity boardEntity = boardrepository.findById(replyDTO.getBoardNum())
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		ReplyEntity entity = ReplyEntity.builder().board(boardEntity).member(memberEntity)
				.contents(replyDTO.getContents()).build();

		replyrepository.save(entity);
	}

	public void replyDelete(Integer replyNum, String username) {
		ReplyEntity replyEntity = replyrepository.findById(replyNum)
				.orElseThrow(() -> new EntityNotFoundException("리플이 없습니다."));
		if (!replyEntity.getMember().getMemberId().equals(username)) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}
		replyrepository.delete(replyEntity);

	}

	// 다운로드 메서드
	public void download(Integer boardNum, HttpServletResponse response, String uploadPath) {
		// 전달된 글 번호로 파일명 확인
		BoardEntity boardentity = boardrepository.findById(boardNum)
				.orElseThrow(() -> new EntityNotFoundException("파일명이  없습니다"));

		// response의 헤더에 파일정보 세팅(1.파일 읽을준비)
		// response.setHeader("Content-Disposition", "attachment;filename=a.jpg");
		try {
			// 상대방에 대한 정보로 뭘 보낼지, 정보를 보낼건데 이름을 `로 할것이다
			response.setHeader("Content-Disposition", // 브라우저에게 이 응답이 파일 다운로드를 위한 것임을 알림.
					"attachment;filename=" + URLEncoder.encode(boardentity.getOriginalName(), "UTF-8")); // 한글 깨지지 않게
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		}

		// 저장된 파일 경로 c;/upload. ~~~~.JPG
		String fullPath = uploadPath + "/" + boardentity.getFileName();

		// 2.파일 읽음
		// 서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력 스트림
		FileInputStream filein = null;
		ServletOutputStream fileout = null;

		try {
			// 통로 열림
			filein = new FileInputStream(fullPath); // 서버의 파일과 프로그램, 입력 스트임(서버입장에서 파일에 저장된걸 읽어옴)
			fileout = response.getOutputStream(); // 프로그램과 클라이언트 출력 스트림

			// Spring의 파일관련 유틸 이용하여 출력 (3.파일 출력중)
			FileCopyUtils.copy(filein, fileout);

			filein.close(); // 스트림안의 데이터 손실되지 않게 잠가야함 그리고 연결 끊는것
			fileout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

//	public List<BoardDTO> getList(String searchWord) {
//
//		// 작성한 순서대로(역순으로)
//		Sort sort = Sort.by(Sort.Direction.DESC, "boardNum");
//		// List<BoardEntity> BoardEntityList = brepository.findAll(sort);
//
//		// 검색조건 1) select * from web5_board where title like '%단어%';
//		// List<BoardEntity> BoardEntityList =
//		// brepository.findByTitleContaining(searchWord, sort);
//
//		// 검색 조건 2)select * from web5_board where title or contenrs like '%단어%';
//		List<BoardEntity> BoardEntityList = brepository
//				.findByTitleContainingOrContentsContainingOrderByTitleDesc(searchWord, searchWord);
//		List<BoardDTO> boardList = new ArrayList<>();
//
//		for (BoardEntity entity : BoardEntityList) {
//
//			BoardDTO dto = new BoardDTO();
//			dto.setBoardNum(entity.getBoardNum());
//			dto.setMemberId(entity.getMember().getMemberId());
//			dto.setMemberName(entity.getMember().getMemberName());
//			dto.setTitle(entity.getTitle());
//			dto.setContents(entity.getContents());
//			dto.setViewCount(entity.getViewCount());
//			dto.setLikeCount(entity.getLikeCount());
//			dto.setOriginalName(entity.getOriginalName());
//			dto.setFileName(entity.getFileName());
//			dto.setCreateDate(entity.getCreateDate());
//			dto.setUpdateDate(entity.getUpdateDate());
//			boardList.add(dto);
//		}
//		log.debug("boardList", boardList);
//
//		return boardList;
//	}

//}
