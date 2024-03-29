package org.zerock.controller;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@RequestMapping("/board/*")
@Controller
@AllArgsConstructor
@Log4j
public class BoardController {
	
	private BoardService service;
	
//	@GetMapping("/list")
//	public void list(Model model) {
//		log.info("컨트롤러 list");
//		model.addAttribute("list", service.getList());
//	}

	@GetMapping("/list")
	public void list(Criteria cri, Model model) {
		log.info("컨트롤러 list");
		model.addAttribute("list", service.getList(cri));
//		model.addAttribute("pageMaker", new PageDTO(cri, 123));
		int total = service.getTotal(cri);
		model.addAttribute("pageMaker", new PageDTO(cri, total));
	}

	
	@GetMapping({"/get","/modify"})
	public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
		
		log.info("/get or modify");
		
		model.addAttribute("board", service.get(bno));
	}
	@PostMapping("/modify")
	public String modify(BoardVO board,@ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
		log.info("modify:" + board);
		
		if(service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
		}
		rttr.addAttribute("pageNum",cri.getPageNum());
		rttr.addAttribute("amount", cri.getAmount());

		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		return "redirect:/board/list";
	}
	
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno, Criteria cri, RedirectAttributes rttr) {
		log.info("컨트롤러 remove:" + bno);
		/* Criteria 클래스에 추가한 메서드로 인해 필요없어짐
		rttr.addAttribute("pageNum",cri.getPageNum());
		rttr.addAttribute("amount", cri.getAmount());

		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		*/
		log.info("remove....." + bno);
		
		List<BoardAttachVO> attachList = service.getAttachList1(bno);
		
		if(service.remove(bno)) {
			deleteFiles(attachList);
			rttr.addFlashAttribute("result", "success");
		}
		
			//list에 위에서 주석처리한 값들을 돌려보내줌
		return "redirect:/board/list" + cri.getListLink();
	}
	
	@GetMapping("/register")
	public void register(){
		
	}
	
	@PostMapping("/register")
	public String register(BoardVO board,RedirectAttributes rttr) {
		
		log.info("register 컨트롤러 --------:"+board);
		// 등록처리 후 , rttr객체로 result라는 변수로 리다이렉트 해서 일회성으로 bno 값을보내줌

		if(board.getAttachList() != null) {
			board.getAttachList().forEach(attach -> log.info("컨트롤러 attach -------" +attach));
		}
		
		log.info("------");
		
		service.register(board);
//		rttr.addFlashAttribute("result", board.getBno());
		
		return "redirect:/board/list";
		}
	
	@GetMapping(value= "/getAttachList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<BoardAttachVO>> getAttachList1(Long bno){
	
		log.info("컨트롤러 getAttachList" + bno);
		
		return new ResponseEntity<List<BoardAttachVO>>(service.getAttachList1(bno), HttpStatus.OK);
	}
	
	//첨부파일 삭제 메서드
	private void deleteFiles(List<BoardAttachVO> attachList) {
		
		if(attachList == null || attachList.size() == 0) {
			return;
		}
		
		log.info("delete attach files.........");
		log.info(attachList);

		
		attachList.forEach(attach -> {
			try {
				// java.nio.file 패키지 사용
				Path file = Paths.get("C:\\upload\\" + attach.getUploadPath() + "\\" 
				+ attach.getUuid() +"_"+ attach.getFileName());
			
				Files.deleteIfExists(file);
			
				if(Files.probeContentType(file).startsWith("image")) {
					
				Path thumbNail = Paths.get("C:\\upload\\" + attach.getUploadPath() + "\\s_" 
				+ attach.getUuid() + "_" + attach.getFileName());
				
				Files.delete(thumbNail);
				}
			}catch (Exception e) {
				log.error("delete file error" + e.getMessage());
			}
			
		});
		
	}
}
