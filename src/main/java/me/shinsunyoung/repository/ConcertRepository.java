package me.shinsunyoung.repository;

import me.shinsunyoung.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    // 중복 데이터 확인 메서드
    boolean existsByDateAndVenueAndCity(String date, String venue, String city);
}
