package org.zerock.mapper;


import java.util.List;

import org.zerock.domain.BoardAttachVO;

public interface BoardAttachMapper {

	public void insert(BoardAttachVO vo);
	
	public void delete(String uuid);

	//특정게시물의 번호로 첨부파일을 찾기위한 작업에 필요한 메서드
	public List<BoardAttachVO> findByBno(Long bno);

	//첨부파일과 폴더내 파일삭제
	public void deleteAll(Long bno);

	//어제등록된 첨부파일의 목록 조회
	public List<BoardAttachVO> getOldFiles();

}
