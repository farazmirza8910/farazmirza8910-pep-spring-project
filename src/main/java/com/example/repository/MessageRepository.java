package com.example.repository;

import com.example.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    // Custom query to find messages by postedBy user ID
    List<Message> findByPostedBy(Integer postedBy);
    
    // Optional method to find recent messages within a specific epoch time
    List<Message> findByTimePostedEpochGreaterThanEqual(Long timePostedEpoch);
}
