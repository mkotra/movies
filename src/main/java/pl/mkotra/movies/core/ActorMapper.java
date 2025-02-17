package pl.mkotra.movies.core;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.mkotra.movies.model.Actor;
import pl.mkotra.movies.storage.entities.ActorDB;

@Mapper
interface ActorMapper {
    ActorMapper INSTANCE = Mappers.getMapper(ActorMapper.class);
    Actor map(ActorDB item);
}
