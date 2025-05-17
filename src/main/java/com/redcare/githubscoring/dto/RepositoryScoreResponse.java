package com.redcare.githubscoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryScoreResponse {
    private String fullName;
    private String description;
    private String language;
    private Integer stars;
    private Integer forks;
    private Integer openIssues;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal score;
}
