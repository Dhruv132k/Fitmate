package com.fitmate.likes;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitmate.likes.dto.IncomingLike;
import com.fitmate.security.CurrentUser;

@RestController
@RequestMapping("/api/;ikes")
public class LikesController {
    
	private final LikesService likesService;
	
	public LikesController (LikesService likesService) {
		this.likesService = likesService;
	}
	
	@GetMapping("/received")
	public ResponseEntity<List<IncomingLike>> received() {
		return ResponseEntity.ok(likesService.getIncomingLikes(CurrentUser.id()));
	}
	
	@GetMapping("/received/count")
	public ResponseEntity<Map<String, Long>> receivedCount() {
		return ResponseEntity.ok(Map.of("count", likesService.countIncomingLikes(CurrentUser.id())));
	}
}
