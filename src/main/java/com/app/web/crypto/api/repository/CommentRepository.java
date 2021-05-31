package com.app.web.crypto.api.repository;

import com.app.web.crypto.api.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.fileMetadata.id = (:id)")
    List<Comment> findAllByFileMetadataId(Long id);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.fileMetadata.id =:id")
    List<Comment> deleteAllByFileMetadataId(@Param("id") Long id);
}
