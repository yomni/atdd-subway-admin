package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public void connect(Station upStation, Station downStation, Integer distance) {
        this.sections.add(new Section(null, upStation, 0));
        this.sections.add(new Section(upStation, downStation, distance));
    }

    public void registerSection(Section section) {
        this.sections.add(section);
    }

    public void removeStation(Station station) {
        this.sections.remove(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.values();
    }

    public List<Station> getOrderedStations() {
        return sections.stationValues();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getName(), line.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
