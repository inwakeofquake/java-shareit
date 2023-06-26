package ru.practicum.shareit.booking;

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
    List<Booking> findByBooker(User user, Sort sort);

    List<Booking> findByBookerAndStartIsAfter(User user, LocalDateTime start, Sort sort);

    List<Booking> findByBookerAndEndIsBefore(User user, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(User user, LocalDateTime start,
                                                            LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndStatus(User user, BookingStatus status, Sort sort);

    List<Booking> findByItemOwner(User user, Sort sort);

    List<Booking> findByItemOwnerAndStartIsAfter(User user, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerAndEndIsBefore(User user, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(User user, LocalDateTime start,
                                                               LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerAndStatus(User user, BookingStatus status, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem(Item item);

    List<Booking> findByItemAndStartIsBeforeAndStatus(Item item, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findByItemAndStartIsAfterAndStatus(Item item, LocalDateTime now, BookingStatus status, Sort sort);

}
