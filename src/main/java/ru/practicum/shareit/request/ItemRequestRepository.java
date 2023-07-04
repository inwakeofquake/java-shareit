package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorOrderByCreatedDesc(User requestor);

    Page<ItemRequest> findAllByOrderByCreatedDesc(Pageable pageable);

    Optional<ItemRequest> findById(Long requestId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}
