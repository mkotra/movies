package pl.mkotra.movies.storage.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.Set;

@Indexed
@Entity
@Immutable
@Table(name = "movies")
public class MovieDB {

    @Id
    private Integer id;

    @Column
    @FullTextField
    @GenericField(name = "title_sort", sortable = Sortable.YES)
    private String title;

    @Column(name = "title_reversed")
    private String titleReversed;

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
