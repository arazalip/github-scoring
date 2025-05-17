package com.redcare.githubscoring.service;

import com.redcare.githubscoring.dto.RepositoryScoreResponse;
import com.redcare.githubscoring.model.GitHubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryScoringService {

    private final GitHubService gitHubService;

    private final WeightedScoringProvider weightedScoringProvider;

    public List<RepositoryScoreResponse> scoreRepositories(String language, LocalDateTime createdAfter) {

        List<GitHubRepository> repositories = gitHubService.searchRepositories(language, createdAfter.toLocalDate());

        final AtomicInteger maxStars = new AtomicInteger(1);
        final AtomicInteger maxForks = new AtomicInteger(1);
        for (GitHubRepository repo : repositories) {
            maxStars.updateAndGet(current -> Math.max(current, repo.getStargazersCount()));
            maxForks.updateAndGet(current -> Math.max(current, repo.getForksCount()));
        }

        return repositories.stream()
                .map(repo -> mapResponse(repo, weightedScoringProvider.score(repo, maxStars.get(), maxForks.get())))
                .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                .collect(Collectors.toList());
    }

    private static RepositoryScoreResponse mapResponse(GitHubRepository repo, BigDecimal score) {
        return RepositoryScoreResponse.builder()
                .fullName(repo.getFullName())
                .description(repo.getDescription())
                .language(repo.getLanguage())
                .stars(repo.getStargazersCount())
                .forks(repo.getForksCount())
                .openIssues(repo.getOpenIssuesCount())
                .createdAt(repo.getCreatedAt())
                .updatedAt(repo.getUpdatedAt())
                .score(score)
                .build();
    }



}