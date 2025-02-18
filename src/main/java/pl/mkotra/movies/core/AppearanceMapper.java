package pl.mkotra.movies.core;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.mkotra.movies.model.Appearance;
import pl.mkotra.movies.storage.entities.AppearanceDB;

@Mapper
interface AppearanceMapper {

    AppearanceMapper INSTANCE = Mappers.getMapper(AppearanceMapper.class);

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieName")
    @Mapping(source = "characterName", target = "characterName")
    Appearance map(AppearanceDB appearanceDB);
}
