package com.redcare.githubscoring.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GitHubRepository {
    private Long id;
    private String name;
    private String fullName;
    private String description;
    private String language;
    private Integer stargazersCount;
    private Integer forksCount;
    private Integer openIssuesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}