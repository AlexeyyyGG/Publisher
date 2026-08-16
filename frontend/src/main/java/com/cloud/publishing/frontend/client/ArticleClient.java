package com.cloud.publishing.frontend.client;

import com.cloud.publishing.common.constants.Parameters;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.common.dto.response.ArticleGetAllDTO;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ArticleClient {
    @Value("${backend.url}")
    private String backendUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public ArticleClient(@Qualifier("apiRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ArticleGetAllDTO> getAll() {
        String finalUrl = backendUrl + Urls.ARTICLES;
        return Arrays.asList(Objects.requireNonNull(
                restTemplate.getForEntity(finalUrl, ArticleGetAllDTO[].class).getBody())
        );
    }

    public ArticleDTO get(int id) {
        String finalUrl = backendUrl + Urls.ARTICLES;
        return restTemplate.getForObject(finalUrl + "/" + id, ArticleDTO.class);
    }

    public void add(ArticleDTO request) {
        String finalUrl = backendUrl + Urls.ARTICLES;
        restTemplate.postForObject(finalUrl, request, ArticleDTO.class);
    }

    public void update(int id, ArticleDTO request) {
        String finalUrl = backendUrl + Urls.ARTICLES;
        restTemplate.put(finalUrl + "/" + id, request);
    }

    public void delete(int id) {
        String finalUrl = backendUrl + Urls.ARTICLES;
        restTemplate.delete(finalUrl + "/" + id);
    }

    public List<EmployeeShort> getCoAuthors(int publicationId) {
        String finalUrl = backendUrl + Urls.ARTICLES + Urls.CO_AUTHORS;
        URI uri = UriComponentsBuilder
                .fromUriString(finalUrl)
                .queryParam(Parameters.PUBLICATION_ID, publicationId)
                .build()
                .toUri();
        return Arrays.asList(Objects.requireNonNull(
                restTemplate.getForEntity(uri, EmployeeShort[].class).getBody()
        ));
    }
}