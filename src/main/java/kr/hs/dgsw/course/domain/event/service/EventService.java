package kr.hs.dgsw.course.domain.event.service;

import kr.hs.dgsw.course.domain.event.dto.EventDetailResponse;
import kr.hs.dgsw.course.domain.event.dto.EventListResponse;
import kr.hs.dgsw.course.domain.event.dto.EventSearchCondition;
import kr.hs.dgsw.course.domain.event.entity.Event;
import kr.hs.dgsw.course.domain.event.entity.EventCategory;
import kr.hs.dgsw.course.domain.event.repository.EventRepository;
import kr.hs.dgsw.course.global.exception.CustomException;
import kr.hs.dgsw.course.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public List<EventListResponse> getEvents(EventCategory category, LocalDate date) {
        return eventRepository.searchEvents(new EventSearchCondition(category, date))
                .stream()
                .map(EventListResponse::from)
                .toList();
    }

    public EventDetailResponse getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        return EventDetailResponse.from(event);
    }
}
