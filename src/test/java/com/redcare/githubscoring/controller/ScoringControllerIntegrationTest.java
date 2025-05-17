package com.redcare.githubscoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redcare.githubscoring.dto.RepositorySearchRequest;
import com.redcare.githubscoring.model.GitHubRepository;
import com.redcare.githubscoring.service.GitHubService;
import com.redcare.githubscoring.service.RepositoryScoringService;
import com.redcare.githubscoring.service.WeightedScoringProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoringController.class)
@Import({RepositoryScoringService.class, WeightedScoringProvider.class})
class ScoringControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GitHubService gitHubService;

    @Test
    void shouldScoreRepositoriesSuccessfully() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        GitHubRepository repo1 = createRepository("owner1/repo1", 100, 50, now.minusDays(10), now.minusDays(5));
        GitHubRepository repo2 = createRepository("owner2/repo2", 200, 100, now.minusDays(20), now.minusDays(2));

        when(gitHubService.searchRepositories(any(), any()))
                .thenReturn(Arrays.asList(repo1, repo2));

        RepositorySearchRequest request = new RepositorySearchRequest("java", LocalDate.now().minusDays(30));

        mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", is("owner2/repo2")))
                .andExpect(jsonPath("$[0].score", greaterThan(0.0)))
                .andExpect(jsonPath("$[1].fullName", is("owner1/repo1")))
                .andExpect(jsonPath("$[1].score", greaterThan(0.0)));
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() throws Exception {
        RepositorySearchRequest request = new RepositorySearchRequest("", null);

        mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[0].field", is("createdAfter")));
    }

    @Test
    void shouldReturnServiceUnavailableWhenGitHubApiFails() throws Exception {
        when(gitHubService.searchRepositories(any(), any()))
                .thenThrow(new RestClientException("GitHub API error"));

        RepositorySearchRequest request = new RepositorySearchRequest("java", LocalDate.now().minusDays(30));

        mockMvc.perform(post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error", is("GitHub API Error")));
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
        repo.setDescription("Test repository");
        return repo;
    }
}
