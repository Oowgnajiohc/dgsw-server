package kr.hs.dgsw.course.global.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.hs.dgsw.course.domain.event.entity.Event;
import kr.hs.dgsw.course.domain.event.entity.EventCategory;
import kr.hs.dgsw.course.domain.event.repository.EventRepository;
import kr.hs.dgsw.course.domain.member.entity.Member;
import kr.hs.dgsw.course.domain.member.repository.MemberRepository;
import kr.hs.dgsw.course.domain.seat.entity.Seat;
import kr.hs.dgsw.course.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${tmdb.now-playing-url}")
    private String tmdbNowPlayingUrl;

    @Value("${tmdb.image-base-url}")
    private String tmdbImageBaseUrl;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            return;
        }

        insertMembers();
        insertConcertAndSports();
        insertMoviesFromTmdb();
    }

    private void insertMembers() {
        memberRepository.save(Member.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("테스터")
                .build());
        memberRepository.save(Member.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("유저")
                .build());
    }

    private void insertConcertAndSports() {
        Event concert = eventRepository.save(Event.builder()
                .title("2025 봄 콘서트")
                .category(EventCategory.CONCERT)
                .description("봄을 맞이하는 특별한 콘서트입니다.")
                .venue("올림픽 체조경기장")
                .eventDate(LocalDateTime.of(2025, 6, 15, 19, 0))
                .thumbnailUrl("https://example.com/concert.jpg")
                .totalSeats(15)
                .basePrice(50000)
                .build());

        String[] concertGrades = {"VIP", "R", "S"};
        int[] concertPrices = {150000, 100000, 50000};
        for (int g = 0; g < concertGrades.length; g++) {
            for (int n = 1; n <= 5; n++) {
                seatRepository.save(Seat.builder()
                        .event(concert)
                        .grade(concertGrades[g])
                        .seatNumber(concertGrades[g] + "-" + String.format("%02d", n))
                        .price(concertPrices[g])
                        .build());
            }
        }

        Event sports = eventRepository.save(Event.builder()
                .title("프로야구 한화 vs 두산")
                .category(EventCategory.SPORTS)
                .description("2025 프로야구 정규시즌 경기")
                .venue("잠실야구장")
                .eventDate(LocalDateTime.of(2025, 6, 20, 18, 30))
                .thumbnailUrl("https://example.com/baseball.jpg")
                .totalSeats(15)
                .basePrice(15000)
                .build());

        String[] sportsGrades = {"1루 응원석", "3루 응원석", "외야"};
        int[] sportsPrices = {20000, 20000, 12000};
        for (int g = 0; g < sportsGrades.length; g++) {
            for (int n = 1; n <= 5; n++) {
                seatRepository.save(Seat.builder()
                        .event(sports)
                        .grade(sportsGrades[g])
                        .seatNumber("R" + (g + 1) + "-" + String.format("%02d", n))
                        .price(sportsPrices[g])
                        .build());
            }
        }
    }

    private void insertMoviesFromTmdb() {
        try {
            TmdbResponse response = RestClient.create().get()
                    .uri(tmdbNowPlayingUrl + "?api_key={key}&language=ko-KR&region=KR", tmdbApiKey)
                    .retrieve()
                    .body(TmdbResponse.class);

            if (response == null || response.results() == null) return;

            List<TmdbMovie> movies = response.results().stream().limit(5).toList();

            String[] venueNames = {
                    "CGV 강남 1관",
                    "롯데시네마 건대입구 2관",
                    "메가박스 코엑스 M관",
                    "CGV 홍대 3관",
                    "롯데시네마 월드타워 1관"
            };
            int[] screeningHours = {10, 13, 16, 19, 21};

            for (int i = 0; i < movies.size(); i++) {
                TmdbMovie movie = movies.get(i);
                String thumbnailUrl = movie.posterPath() != null
                        ? tmdbImageBaseUrl + movie.posterPath()
                        : null;

                LocalDateTime screeningTime = LocalDateTime.now()
                        .plusDays(1).withHour(screeningHours[i]).withMinute(0).withSecond(0).withNano(0);

                Event event = eventRepository.save(Event.builder()
                        .title(movie.title())
                        .category(EventCategory.MOVIE)
                        .description(movie.overview())
                        .venue(venueNames[i])
                        .eventDate(screeningTime)
                        .thumbnailUrl(thumbnailUrl)
                        .totalSeats(10)
                        .basePrice(15000)
                        .build());

                for (int n = 1; n <= 10; n++) {
                    seatRepository.save(Seat.builder()
                            .event(event)
                            .grade("일반")
                            .seatNumber("A-" + String.format("%02d", n))
                            .price(15000)
                            .build());
                }

                log.info("영화 등록: {} @ {} {}시", movie.title(), venueNames[i], screeningHours[i]);
            }
        } catch (Exception e) {
            log.warn("TMDB 영화 조회 실패, 기본 영화 데이터로 대체합니다. 원인: {}", e.getMessage());
            insertFallbackMovie();
        }
    }

    private void insertFallbackMovie() {
        Event movie = eventRepository.save(Event.builder()
                .title("어벤져스: 리턴")
                .category(EventCategory.MOVIE)
                .description("마블 시네마틱 유니버스 최신작")
                .venue("CGV 강남")
                .eventDate(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .thumbnailUrl("https://example.com/movie.jpg")
                .totalSeats(10)
                .basePrice(15000)
                .build());

        for (int n = 1; n <= 10; n++) {
            seatRepository.save(Seat.builder()
                    .event(movie)
                    .grade("일반")
                    .seatNumber("A-" + String.format("%02d", n))
                    .price(15000)
                    .build());
        }
    }

    record TmdbResponse(List<TmdbMovie> results) {}

    record TmdbMovie(
            String title,
            String overview,
            @JsonProperty("poster_path") String posterPath
    ) {}
}
