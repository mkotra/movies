package pl.mkotra.movies.storage.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AppearanceDBId implements Serializable {

    private int movieId;
    private int actorId;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    @Override
    public int hashCode() {
        return movieId + actorId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AppearanceDBId that = (AppearanceDBId) obj;
        return movieId == that.movieId && actorId == that.actorId;
    }
}

