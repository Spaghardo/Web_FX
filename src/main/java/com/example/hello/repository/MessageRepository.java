package com.example.hello.repository;

import com.example.hello.model.Message;
import com.example.hello.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverOrderByTimestampDesc(User receiver);
    List<Message> findBySenderOrderByTimestampDesc(User sender);
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(User sender1, User receiver1, User receiver2, User sender2);
    List<Message> findBySenderOrReceiverOrderByTimestampDesc(User sender, User receiver);
}
