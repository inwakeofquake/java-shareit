package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner, Sort sort);

    Item findByName(String name);

    @Query("select i from Item i where i.available=true AND (upper(i.name) like upper(concat('%', :text, '%')) or upper(i.description) like upper(concat('%', :text, '%')))")
    List<Item> search(@Param("text") String text);
}