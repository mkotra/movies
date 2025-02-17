package pl.mkotra.movies.core;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.mkotra.movies.model.Movie;
import pl.mkotra.movies.storage.entities.MovieDB;

@Mapper
interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);
    Movie map(MovieDB item);
}
