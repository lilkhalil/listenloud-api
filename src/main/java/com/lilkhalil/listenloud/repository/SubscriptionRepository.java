package com.lilkhalil.listenloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lilkhalil.listenloud.model.Subscription;
import com.lilkhalil.listenloud.model.SubscriptionKey;
import com.lilkhalil.listenloud.model.User;

import jakarta.transaction.Transactional;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionKey> {
    
    @Query("SELECT s.subscriber FROM Subscription s WHERE s.publisher = ?1")
    List<User> findSubscribersByUser(User user);

    @Query("SELECT s.publisher FROM Subscription s WHERE s.subscriber = ?1")
    List<User> findSubscriptionsByUser(User user);

    @Transactional
    void deleteBySubscriberAndPublisher(User subscriber, User publisher);

    @Transactional
    void deleteBySubscriberAndPublisherIn(User subscriber, List<User> publishers);

}
