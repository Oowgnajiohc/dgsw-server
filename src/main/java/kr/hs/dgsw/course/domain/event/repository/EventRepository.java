package kr.hs.dgsw.course.domain.event.repository;

import kr.hs.dgsw.course.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {
}
