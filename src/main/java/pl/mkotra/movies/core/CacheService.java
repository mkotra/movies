package pl.mkotra.movies.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.mkotra.movies.storage.ActorRepository;
import pl.mkotra.movies.storage.MovieRepository;

@Service
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    private final Cache<CacheKey, Long> cache;
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;

    public CacheService(MovieRepository movieRepository, ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.cache = Caffeine.newBuilder()
                .maximumSize(CacheKey.values().length)
                .build();
    }

    public Long get(CacheKey cacheKey) {
        return cache.get(cacheKey, _ -> load(cacheKey));
    }

    public void invalidate(CacheKey cacheKey) {
        cache.invalidate(cacheKey);
        logger.info("Cache key {} invalidated", cacheKey);
    }

    public long load(CacheKey cacheKey) {
        logger.info("Loading cache key {} ...", cacheKey);
        return switch (cacheKey) {
            case ACTORS_COUNT -> actorRepository.count();
            case MOVIES_COUNT -> movieRepository.count();
        };
    }

    public enum CacheKey {
        ACTORS_COUNT,
        MOVIES_COUNT
    }
}

