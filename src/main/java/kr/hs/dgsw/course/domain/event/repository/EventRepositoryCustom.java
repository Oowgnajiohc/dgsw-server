package kr.hs.dgsw.course.domain.event.repository;

import kr.hs.dgsw.course.domain.event.dto.EventSearchCondition;
import kr.hs.dgsw.course.domain.event.entity.Event;

import java.util.List;

public interface EventRepositoryCustom {

    List<Event> searchEvents(EventSearchCondition condition);
}
