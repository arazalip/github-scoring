package com.redcare.githubscoring.service;

import com.redcare.githubscoring.model.GitHubRepository;
import java.math.BigDecimal;

/**
 * Interface for providing scoring functionality.
 * Implementations of this interface should provide methods to calculate
 * repository scores based on various metrics like stars, forks, and recency.
 */
public interface ScoringProvider {
    BigDecimal score(GitHubRepository repo, double maxStars, double maxForks);
    BigDecimal recencyScore(GitHubRepository repo);
}
