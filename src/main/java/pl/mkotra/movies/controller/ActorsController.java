package pl.mkotra.movies.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mkotra.movies.api.ActorsApi;
import pl.mkotra.movies.core.ActorService;
import pl.mkotra.movies.model.Actor;
import pl.mkotra.movies.model.Appearance;

import java.util.List;

@RestController
class ActorsController implements ActorsApi {

    ActorService actorService;
    PageHeadersFactory pageHeadersFactory;

    ActorsController(ActorService actorService, PageHeadersFactory pageHeadersFactory) {
        this.actorService = actorService;
        this.pageHeadersFactory = pageHeadersFactory;
    }

    @Override
    public ResponseEntity<List<Actor>> actorsGet(Integer page, Integer pageSize, @RequestParam(defaultValue = "%") String name) {
        Page<Actor> actorPage = actorService.getActors(name, page, pageSize);

        return ResponseEntity.ok()
                .headers(pageHeadersFactory.create(actorPage))
                .body(actorPage.getContent());
    }

    @Override
    public ResponseEntity<List<Appearance>> actorsIdAppearancesGet(Integer id, Integer page, Integer pageSize) {
        Page<Appearance> actorPage = actorService.getAppearances(id, page, pageSize);

        return ResponseEntity.ok()
                .headers(pageHeadersFactory.create(actorPage))
                .body(actorPage.getContent());
    }

    @Override
    public ResponseEntity<Actor> actorsIdGet(Integer id) {
        return actorService.getActor(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
