package pl.mkotra.movies.storage.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "appearances")
public class AppearanceDB {

    @EmbeddedId
    private AppearanceDBId id;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieDB movie;

    @ManyToOne
    @MapsId("actorId")
    @JoinColumn(name = "actor_id", nullable = false)
    private ActorDB actor;

    @Column(name = "character_name")
    private String characterName;

    public AppearanceDBId getId() {
        return id;
    }

    public void setId(AppearanceDBId id) {
        this.id = id;
    }

    public MovieDB getMovie() {
        return movie;
    }

    public void setMovie(MovieDB movie) {
        this.movie = movie;
    }

    public ActorDB getActor() {
        return actor;
    }

    public void setActor(ActorDB actor) {
        this.actor = actor;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
