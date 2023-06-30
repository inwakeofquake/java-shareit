package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker(User user, Pageable pageable);

    Page<Booking> findByBookerAndStartIsAfter(User user, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerAndEndIsBefore(User user, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(User user, LocalDateTime start,
                                                            LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndStatus(User user, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwner(User user, Pageable pageable);

    Page<Booking> findByItemOwnerAndStartIsAfter(User user, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemOwnerAndEndIsBefore(User user, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(User user, LocalDateTime start,
                                                               LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerAndStatus(User user, BookingStatus status, Pageable pageable);


    List<Booking> findByItem(Item item);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemAndStartIsBeforeAndStatus(Item item, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findByItemAndStartIsAfterAndStatus(Item item, LocalDateTime now, BookingStatus status, Sort sort);
}
