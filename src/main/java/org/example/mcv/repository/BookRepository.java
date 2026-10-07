package org.example.mcv.repository;

import org.example.mcv.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author", "categories", "detail"})
    @Query("select distinct book from Book book")
    List<Book> findAllWithAssociations();

    @EntityGraph(attributePaths = {"author", "categories", "detail"})
    @Query("select book from Book book where book.id = :id")
    Optional<Book> findWithAssociationsById(@Param("id") Long id);
}