package com.redcare.githubscoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.redcare.githubscoring.exception.RepositoryScoringException;
import com.redcare.githubscoring.model.GitHubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;
    
    @Value("${github.api.base-url:https://api.github.com}")
    private String baseUrl;

    @Value("${github.api.path:/search/repositories}")
    private String apiPath;

    @Cacheable(value = "repositories", key = "#language + '-' + #createdAfter")
    public List<GitHubRepository> searchRepositories(String language, LocalDate createdAfter) {
        String query = String.format("language:%s created:>%s", 
            language, 
            createdAfter.format(DateTimeFormatter.ISO_DATE));

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path(apiPath)
            .queryParam("q", query)
            .queryParam("sort", "stars")
            .queryParam("order", "desc")
            .build()
            .toUriString();

        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        JsonNode itemsNode = Objects.requireNonNull(root).get("items");

        CollectionType repoListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, GitHubRepository.class);
        try {
            return objectMapper.readValue(itemsNode.traverse(), repoListType);
        } catch (Exception e) {
            throw new RepositoryScoringException("Failed to parse GitHub repositories", e);
        }
    }
} 