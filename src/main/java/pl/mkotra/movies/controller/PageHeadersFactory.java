package pl.mkotra.movies.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
class PageHeadersFactory {

    HttpHeaders create(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Size", String.valueOf(page.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.getTotalPages()));
        headers.add("X-Page-Number", String.valueOf(page.getNumber()));
        headers.add("X-Page-Size", String.valueOf(page.getSize()));
        return headers;
    }
}
