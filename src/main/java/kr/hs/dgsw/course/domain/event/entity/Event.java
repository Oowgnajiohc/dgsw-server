package kr.hs.dgsw.course.domain.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    private String thumbnailUrl;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int basePrice;

    @Builder
    public Event(String title, EventCategory category, String description, String venue,
                 LocalDateTime eventDate, String thumbnailUrl, int totalSeats, int basePrice) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.venue = venue;
        this.eventDate = eventDate;
        this.thumbnailUrl = thumbnailUrl;
        this.totalSeats = totalSeats;
        this.basePrice = basePrice;
    }
}
