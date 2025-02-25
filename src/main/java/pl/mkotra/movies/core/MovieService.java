package pl.mkotra.movies.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.MovieRepository;
import pl.mkotra.movies.storage.entities.MovieDB;

import java.util.List;
import java.util.Optional;

import static pl.mkotra.movies.core.CacheService.CacheKey.MOVIES_COUNT;

@Service
public class MovieService {

    private static final Sort MOVIES_SORT = Sort.by(Sort.Direction.ASC, "title");

    private final MovieRepository movieRepository;
    private final CacheService cachingService;

    MovieService(MovieRepository movieRepository, CacheService cachingService) {
        this.movieRepository = movieRepository;
        this.cachingService = cachingService;
    }

    public Optional<Movie> getMovie(Integer id) {
        return movieRepository.findById(id).map(MovieMapper.INSTANCE::map);
    }

    public Page<Movie> getMovies(String title, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, MOVIES_SORT);

        boolean isAllWildcard = title.chars().allMatch(ch -> ch == '%');
        SearchType searchType = isAllWildcard ? SearchType.ALL_WILDCARD
                : title.startsWith("%") ? SearchType.TITLE_REVERSED
                : SearchType.TITLE_NORMAL;

        Page<MovieDB> movieDBPage = switch (searchType) {
            case ALL_WILDCARD -> createPageWithoutCount(pageNumber, pageSize);
            case TITLE_REVERSED -> movieRepository.findByTitleReversedLike(StringUtils.reverse(title), pageRequest);
            case TITLE_NORMAL -> movieRepository.findByTitleLike(title, pageRequest);
        };

        List<Movie> movies = movieDBPage.getContent().stream()
                .map(MovieMapper.INSTANCE::map)
                .toList();

        return new PageImpl<>(movies, movieDBPage.getPageable(), movieDBPage.getTotalElements());
    }

    private Page<MovieDB> createPageWithoutCount(int pageNumber, int pageSize) {
        List<MovieDB> movieDBS = movieRepository.findWithoutCount(pageSize, pageNumber);
        long totalCount = cachingService.get(MOVIES_COUNT);
        return new PageImpl<>(movieDBS, PageRequest.of(pageNumber, pageSize, MOVIES_SORT), totalCount);
    }

    private enum SearchType {
        ALL_WILDCARD, TITLE_REVERSED, TITLE_NORMAL
    }
}
