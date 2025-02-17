package pl.mkotra.movies.storage.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "movies")
public class MovieDB {

    @Id
    private Integer id;

    @Column
    private String title;

    @Column
    private String year;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private Set<AppearanceDB> appearances;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Set<AppearanceDB> getAppearances() {
        return appearances;
    }

    public void setAppearances(Set<AppearanceDB> appearances) {
        this.appearances = appearances;
    }
}
