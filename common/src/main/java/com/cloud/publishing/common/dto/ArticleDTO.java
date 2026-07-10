package com.cloud.publishing.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record ArticleDTO(
        @NotNull(message = "Необходимо выбрать журнал или газету")
        Integer publicationId,
        @NotNull(message = "Необходимо выбрать рубрику")
        Integer categoryId,
        @NotBlank(message = "Название статьи не должно быть пустым")
        @Size(max = 30, message = "Название статьи не должно превышать 100 символов")
        String name,
        @NotBlank(message = "Содержание статьи не должно быть пустым")
        @Size(min = 50, message = "Содержание статьи должно содержать минимум 50 символов")
        String content,
        Set<Integer> coAuthorsIds
) {
}