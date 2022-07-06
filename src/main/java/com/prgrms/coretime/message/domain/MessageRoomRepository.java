package com.prgrms.coretime.message.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

  @Query(
      value = "select * from message_room "
          + "where created_from=:created_from AND is_anonymous=:is_anonymous "
          + "AND (initial_receiver_id=:first_interlocutor_id AND initial_sender_id=:second_interlocutor_id)  "
          + "OR (initial_receiver_id=:second_interlocutor_id AND initial_sender_id=:first_interlocutor_id)",
      nativeQuery = true)
  Optional<MessageRoom> findMessageRoomByInfo(@Param("created_from") Long createdFrom,
      @Param("is_anonymous") Boolean isAnonymous, @Param("first_interlocutor_id") Long firstInterlocutorId,
      @Param("second_interlocutor_id") Long secondInterlocutorId);

  @Query(
      value = "select message_room_id from message_room "
          + "where created_from=:created_from AND is_anonymous=:is_anonymous "
          + "AND (initial_receiver_id=:first_interlocutor_id AND initial_sender_id=:second_interlocutor_id)  "
          + "OR (initial_receiver_id=:second_interlocutor_id AND initial_sender_id=:first_interlocutor_id)",
      nativeQuery = true)
  Optional<Long> findIdByInfo(@Param("created_from") Long createdFrom,
      @Param("is_anonymous") Boolean isAnonymous, @Param("first_interlocutor_id") Long firstInterlocutorId,
      @Param("second_interlocutor_id") Long secondInterlocutorId);

  @Query(
      value = "select exists (select * from message_room "
          + "where created_from=:created_from AND is_anonymous=:is_anonymous "
          + "AND (initial_receiver_id=:first_interlocutor_id AND initial_sender_id=:second_interlocutor_id)  "
          + "OR (initial_receiver_id=:second_interlocutor_id AND initial_sender_id=:first_interlocutor_id))",
      nativeQuery = true)
  boolean existsByInfo(@Param("created_from") Long createdFrom,
      @Param("is_anonymous") Boolean isAnonymous, @Param("first_interlocutor_id") Long firstInterlocutorId,
      @Param("second_interlocutor_id") Long secondInterlocutorId);

  @Query(
      value = "select m from Message m join fetch m.messageRoom join fetch m.writer where m.messageRoom.id=:id",
      countQuery = "select count(m) from Message m where m.id=:id")
  Page<Message> findMessagesByMessageRoomId(@Param("id") Long messageRoomId, Pageable pageable);

}