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
    private final String baseArticleUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public ArticleClient(
            @Qualifier("apiRestTemplate") RestTemplate restTemplate,
            @Value("${backend.url}") String backendUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseArticleUrl = backendUrl + Urls.ARTICLES;
    }

    public List<ArticleGetAllDTO> getAll() {
        return Arrays.asList(Objects.requireNonNull(
                restTemplate.getForEntity(baseArticleUrl, ArticleGetAllDTO[].class).getBody())
        );
    }

    public ArticleDTO get(int id) {
        return restTemplate.getForObject(baseArticleUrl + "/" + id, ArticleDTO.class);
    }

    public void add(ArticleDTO request) {
        restTemplate.postForObject(baseArticleUrl, request, ArticleDTO.class);
    }

    public void update(int id, ArticleDTO request) {
        restTemplate.put(baseArticleUrl + "/" + id, request);
    }

    public void delete(int id) {
        restTemplate.delete(baseArticleUrl + "/" + id);
    }

    public List<EmployeeShort> getCoAuthors(int publicationId) {
        String finalUrl = baseArticleUrl + Urls.CO_AUTHORS;
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