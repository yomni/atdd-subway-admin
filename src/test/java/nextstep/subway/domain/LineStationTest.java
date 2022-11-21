package nextstep.subway.domain;

import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DatabaseCleanup.class)
public class LineStationTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();
        stationRepository.saveAll(Arrays.asList(
                new Station("판교역"),
                new Station("이매역"),
                new Station("경기광주역")
        ));
        lineRepository.save(new Line("경강선", "bg-blue-600"));
    }

    @DisplayName("라인을 생성하고, station을 linestation으로 추가한다.")
    @Test
    void 라인생성_addLineStation테스트() {
        // given : 라인과 station을 생성하고, LineStation도 생성
        Line line = lineRepository.getById(1L);
        Station upStation = stationRepository.getById(1L);
        Station downStation = stationRepository.getById(2L);

        LineStation firstLineStation = new LineStation(upStation, downStation, 100);
        LineStation secondLineStation = new LineStation(null, upStation, 0);

        // when : 라인에 linestation을 추가 후 save
        line.addLineStation(firstLineStation);
        line.addLineStation(secondLineStation);
        lineRepository.save(line);
        flushAndClear();

        // then : line의 linestations에 잘 들어갔는 지 확인
        Optional<Line> getLine = lineRepository.findById(1L);
        assertThat(getLine).isPresent();
        assertThat(getLine.get().getLineStations()).containsAll(Arrays.asList(firstLineStation, secondLineStation));
    }

    @DisplayName("역 사이에 새로운 역을 등록하는 경우")
    @Test
    void 역_사이에_새로운역_등록() {
        // line과 station 기반으로 lineStation 생성
        Line 신분당선 = lineRepository.getById(1L);
        Station 판교역 = stationRepository.getById(1L);
        Station 경기광주역 = stationRepository.getById(3L);
        신분당선.addLineStation(new LineStation(null, 판교역, 0));
        신분당선.addLineStation(new LineStation(판교역, 경기광주역, 100));

        // when : 라인에 linestation을 추가 후 중간에 midLineStation을 추가
        Station 이매역 = stationRepository.getById(2L);
        신분당선.addSection(new LineStation(판교역, 이매역, 40));
        lineRepository.save(신분당선);
        flushAndClear();

        // then : line의 linestations에 잘 들어갔는 지 확인
        Optional<Line> getLine = lineRepository.findById(1L);
        assertThat(getLine).isPresent();
        assertThat(getLine.get().getLineStations()).hasSize(3);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void 새로운역_상행_종점_등록() {
        // line과 station 기반으로 lineStation 생성
        Line 신분당선 = lineRepository.getById(1L);
        Station 이매역 = stationRepository.getById(2L);
        Station 경기광주역 = stationRepository.getById(3L);
        신분당선.addLineStation(new LineStation(null, 이매역, 0));
        신분당선.addLineStation(new LineStation(이매역, 경기광주역, 100));

        // when : 라인에 linestation을 추가 후 중간에 midLineStation을 추가
        Station 판교역 = stationRepository.getById(1L);
        신분당선.addSection(new LineStation(판교역, 이매역, 40));
        lineRepository.save(신분당선);
        flushAndClear();

        // then : line의 linestations에 잘 들어갔는 지 확인
        Optional<Line> getLine = lineRepository.findById(1L);
        assertThat(getLine).isPresent();
        assertThat(getLine.get().getLineStations()).hasSize(3);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
