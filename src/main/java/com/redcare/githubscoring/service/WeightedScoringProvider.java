package com.redcare.githubscoring.service;

import com.redcare.githubscoring.model.GitHubRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class WeightedScoringProvider implements ScoringProvider {

    @Value("${scoring.weights.stars:0.4}")
    private double starsWeight;

    @Value("${scoring.weights.forks:0.3}")
    private double forksWeight;

    @Value("${scoring.weights.recency:0.3}")
    private double recencyWeight;

    /**
     * Calculates a normalized score for a GitHub repository based on stars, forks, and recency.
     * The score is computed using weighted components:
     * - Stars score: normalized by maximum stars count and weighted by starsWeight
     * - Forks score: normalized by maximum forks count and weighted by forksWeight
     * - Recency score: calculated based on repository age and last update time, weighted by recencyWeight
     *
     * @param repo The GitHub repository to score
     * @param maxStars The maximum number of stars among all repositories being scored
     * @param maxForks The maximum number of forks among all repositories being scored
     * @return A BigDecimal representing the normalized score, rounded to 2 decimal places
     */
    public BigDecimal score(GitHubRepository repo, double maxStars, double maxForks) {
        double normStars = (repo.getStargazersCount() / maxStars) * starsWeight;
        double normForks = (repo.getForksCount() / maxForks) * forksWeight;
        double normRecency = recencyScore(repo).doubleValue() * recencyWeight;
        log.debug("raw scores. repo:{} - stars: {}, forks: {}, recency: {}", repo.getName(), normStars, normForks, normRecency);
        double score = normStars + normForks + normRecency;
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates a recency score for a GitHub repository based on its creation and last update dates.
     * The score is computed as the ratio of total repository age to time since last update.
     * A higher score indicates a repository that is both old and actively maintained.
     *
     * @param repo The GitHub repository to calculate the recency score for
     * @return A BigDecimal representing the recency score, where higher values indicate
     *         repositories that are both established and actively maintained
     */
    public BigDecimal recencyScore(GitHubRepository repo) {
        long daysSinceUpdate = ChronoUnit.DAYS.between(repo.getUpdatedAt(), LocalDateTime.now()) + 1;
        long daysSinceCreatedAt = ChronoUnit.DAYS.between(repo.getCreatedAt(), LocalDateTime.now()) + 1;
        return BigDecimal.valueOf((double) daysSinceCreatedAt / daysSinceUpdate).setScale(2, RoundingMode.HALF_UP);
    }
}
