package com.fitmate.discovery;

import com.fitmate.discovery.dto.CandidateCard;
import com.fitmate.security.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping("/feed")
    public ResponseEntity<List<CandidateCard>> feed() {
        return ResponseEntity.ok(discoveryService.getFeed(CurrentUser.id()).getCandidates());
    }
}
