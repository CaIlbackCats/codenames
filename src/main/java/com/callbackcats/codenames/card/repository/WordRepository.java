package com.callbackcats.codenames.card.repository;

import com.callbackcats.codenames.card.domain.GameLanguage;
import com.callbackcats.codenames.card.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    @Query("SELECT w FROM Word w WHERE w.language= :language")
    public List<Word> findWordsByLanguage(@Param("language") GameLanguage language);
}
