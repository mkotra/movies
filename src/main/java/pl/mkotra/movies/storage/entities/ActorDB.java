package pl.mkotra.movies.storage.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "actors")
public class ActorDB {

    @Id
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    private Set<AppearanceDB> appearances;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AppearanceDB> getAppearances() {
        return appearances;
    }

    public void setAppearances(Set<AppearanceDB> appearances) {
        this.appearances = appearances;
    }
}
