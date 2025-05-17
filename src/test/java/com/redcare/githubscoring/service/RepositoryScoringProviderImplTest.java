package com.redcare.githubscoring.service;

import com.redcare.githubscoring.dto.RepositoryScoreResponse;
import com.redcare.githubscoring.model.GitHubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "scoring.weights.stars=0.4",
        "scoring.weights.forks=0.3",
        "scoring.weights.recency=0.3"
})
class RepositoryScoringProviderImplTest {

    @MockBean
    private GitHubService gitHubService;

    @Autowired
    private RepositoryScoringService scoringService;

    @Test
    void shouldScoreRepositoriesCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        GitHubRepository repo1 = createRepository("owner1/repo1", 100, 50, now.minusDays(10), now.minusDays(5));
        GitHubRepository repo2 = createRepository("owner2/repo2", 200, 100, now.minusDays(20), now.minusDays(2));

        when(gitHubService.searchRepositories(any(), any()))
                .thenReturn(Arrays.asList(repo1, repo2));

        List<RepositoryScoreResponse> results = scoringService.scoreRepositories("java", now.minusDays(30));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getFullName()).isEqualTo("owner2/repo2");
        assertThat(results.get(0).getScore()).isGreaterThan(results.get(1).getScore());
    }

    @Test
    void shouldHandleEmptyRepositoryList() {
        when(gitHubService.searchRepositories(any(), any())).thenReturn(List.of());
        List<RepositoryScoreResponse> results = scoringService.scoreRepositories("java", LocalDateTime.now());
        assertThat(results).isEmpty();
    }

    @Test
    void shouldCalculateRecencyScoreCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        GitHubRepository repo = createRepository("owner/repo", 100, 50, now.minusDays(100), now.minusDays(10));

        when(gitHubService.searchRepositories(any(), any())).thenReturn(List.of(repo));

        List<RepositoryScoreResponse> results = scoringService.scoreRepositories("java", now.minusDays(200));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getScore()).isGreaterThan(BigDecimal.ZERO);
    }

    private GitHubRepository createRepository(String fullName, int stars, int forks,
                                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        GitHubRepository repo = new GitHubRepository();
        repo.setFullName(fullName);
        repo.setStargazersCount(stars);
        repo.setForksCount(forks);
        repo.setCreatedAt(createdAt);
        repo.setUpdatedAt(updatedAt);
        repo.setOpenIssuesCount(5);
        repo.setLanguage("Java");
        repo.setDescription("Description");
        return repo;
    }
}