package com.redcare.githubscoring.controller;

import com.redcare.githubscoring.dto.RepositoryScoreResponse;
import com.redcare.githubscoring.dto.RepositorySearchRequest;
import com.redcare.githubscoring.service.RepositoryScoringService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScoringController {

    private final RepositoryScoringService scoringService;

    @Operation(
            summary = "Scores GitHub repositories based on the provided search criteria.",
            description = "The scoring takes into account stars, forks, and recency of updates." +
                            "Results are sorted by score in descending order."
    )
    @PostMapping("/score")
    public ResponseEntity<List<RepositoryScoreResponse>> scoreRepositories(
            @Valid @RequestBody RepositorySearchRequest request) {
        List<RepositoryScoreResponse> scores = scoringService.scoreRepositories(
            request.getLanguage(),
            request.getCreatedAfter().atStartOfDay()
        );
        return ResponseEntity.ok(scores);
    }
} 