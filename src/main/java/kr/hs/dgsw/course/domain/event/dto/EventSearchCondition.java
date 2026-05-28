package kr.hs.dgsw.course.domain.event.dto;

import kr.hs.dgsw.course.domain.event.entity.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EventSearchCondition {

    private EventCategory category;
    private LocalDate date;
}
