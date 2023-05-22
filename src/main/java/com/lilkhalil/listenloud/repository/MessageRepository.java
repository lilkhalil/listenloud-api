package com.lilkhalil.listenloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Message;
import com.lilkhalil.listenloud.model.User;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT MAX(m.id) FROM Message m WHERE m.sender = ?1 GROUP BY m.recipient ORDER BY COUNT(recipient) DESC")
    List<Long> findLatestMessageOfDialogue(User sender);

    List<Message> findBySenderInAndRecipientInOrderByTimestamp(List<User> sender, List<User> recipient);

}
