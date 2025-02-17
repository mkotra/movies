package pl.mkotra.movies.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageHeadersFactoryTest {

    private final PageHeadersFactory pageHeadersFactory = new PageHeadersFactory();

    @Test
    void shouldCreateHeadersWithCorrectValues() {
        //given
        Page<?> page = mock(Page.class);
        when(page.getTotalElements()).thenReturn(100L);
        when(page.getTotalPages()).thenReturn(10);
        when(page.getNumber()).thenReturn(1);
        when(page.getSize()).thenReturn(10);

        //when
        HttpHeaders headers = pageHeadersFactory.create(page);

        //then
        assertThat(headers)
                .containsEntry("X-Total-Size", List.of("100"))
                .containsEntry("X-Total-Pages", List.of("10"))
                .containsEntry("X-Page-Number", List.of("1"))
                .containsEntry("X-Page-Size", List.of("10"));
    }
}