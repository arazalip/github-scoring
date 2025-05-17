package com.redcare.githubscoring.service;

import com.redcare.githubscoring.model.GitHubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WeightedScoringProviderTest {

    @InjectMocks
    private WeightedScoringProvider weightedScoringProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weightedScoringProvider, "starsWeight", 0.4);
        ReflectionTestUtils.setField(weightedScoringProvider, "forksWeight", 0.3);
        ReflectionTestUtils.setField(weightedScoringProvider, "recencyWeight", 0.3);
    }

    @Nested
    class ScoreCalculationTests {

        @Test
        void shouldCalculateScoreForHighStarsAndForks() {
            GitHubRepository repo = createRepository(1000, 500,
                LocalDateTime.now().minusDays(100), 
                LocalDateTime.now().minusDays(10));

            BigDecimal score = weightedScoringProvider.score(repo, 1000.0, 500.0);

            assertThat(score).isEqualTo(new BigDecimal("3.45"));
        }

        @Test
        void shouldCalculateScoreForLowStarsAndForks() {
            GitHubRepository repo = createRepository(10, 5,
                LocalDateTime.now().minusDays(100), 
                LocalDateTime.now().minusDays(10));

            BigDecimal score = weightedScoringProvider.score(repo, 1000.0, 500.0);

            assertThat(score).isEqualTo(new BigDecimal("2.76"));
        }

        @Test
        void shouldHandleZeroStarsAndForks() {
            GitHubRepository repo = createRepository(0, 0,
                LocalDateTime.now().minusDays(100), 
                LocalDateTime.now().minusDays(10));

            BigDecimal score = weightedScoringProvider.score(repo, 1000.0, 500.0);

            assertThat(score).isEqualTo(new BigDecimal("2.75"));
        }

        @Test
        void shouldHandleMaximumStarsAndForks() {
            GitHubRepository repo = createRepository(1000, 500,
                LocalDateTime.now().minusDays(100), 
                LocalDateTime.now().minusDays(10));

            BigDecimal score = weightedScoringProvider.score(repo, 1000.0, 500.0);

            assertThat(score).isEqualTo(new BigDecimal("3.45"));
        }
    }

    @Nested
    class RecencyScoreTests {

        @Test
        void shouldCalculateHighRecencyScore() {
            GitHubRepository repo = createRepository(100, 50,
                LocalDateTime.now().minusDays(100), 
                LocalDateTime.now().minusDays(1));

            BigDecimal recencyScore = weightedScoringProvider.recencyScore(repo);

            assertThat(recencyScore).isEqualTo(new BigDecimal("50.50"));
        }

        @Test
        void shouldCalculateLowRecencyScore() {
            GitHubRepository repo = createRepository(100, 50,
                LocalDateTime.now().minusDays(100), 
                LocalDateTime.now().minusDays(90));

            BigDecimal recencyScore = weightedScoringProvider.recencyScore(repo);

            assertThat(recencyScore).isEqualTo(new BigDecimal("1.11"));
        }

        @Test
        void shouldHandleRepositoryCreatedToday() {
            GitHubRepository repo = createRepository(100, 50,
                LocalDateTime.now(), 
                LocalDateTime.now());

            BigDecimal recencyScore = weightedScoringProvider.recencyScore(repo);

            assertThat(recencyScore).isEqualTo(new BigDecimal("1.00"));
        }
    }

    private GitHubRepository createRepository(int stars, int forks, 
                                           LocalDateTime createdAt, 
                                           LocalDateTime updatedAt) {
        GitHubRepository repo = new GitHubRepository();
        repo.setStargazersCount(stars);
        repo.setForksCount(forks);
        repo.setCreatedAt(createdAt);
        repo.setUpdatedAt(updatedAt);
        return repo;
    }
} 