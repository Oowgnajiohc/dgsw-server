package kr.hs.dgsw.course.domain.event.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hs.dgsw.course.domain.event.dto.EventSearchCondition;
import kr.hs.dgsw.course.domain.event.entity.Event;
import kr.hs.dgsw.course.domain.event.entity.EventCategory;
import kr.hs.dgsw.course.domain.event.entity.QEvent;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Event> searchEvents(EventSearchCondition condition) {
        QEvent event = QEvent.event;

        return queryFactory
                .selectFrom(event)
                .where(
                        categoryEq(condition.getCategory()),
                        dateEq(condition.getDate())
                )
                .orderBy(event.eventDate.asc())
                .fetch();
    }

    private BooleanExpression categoryEq(EventCategory category) {
        return category != null ? QEvent.event.category.eq(category) : null;
    }

    private BooleanExpression dateEq(LocalDate date) {
        if (date == null) return null;
        return QEvent.event.eventDate.between(
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );
    }
}
