package com.prgrms.coretime.message.domain;

import com.prgrms.coretime.message.dto.MessageRoomsWithLastMessages;
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

    @Query(
      value =
          "select mr.message_room_id as messageRoomId, mr.is_anonymous as isAnonymous, mr.initial_receiver_id as initialReceiverId, mr.initial_sender_id as initialSenderId, m1.created_at as createdAt, m1.content as content "
              + "from message_room as mr "
              + "inner join message as m1 on mr.message_room_id=m1.message_room_id "
              + "inner join (select max(created_at) as max_created_at, message_room_id "
              + "from message "
              + "group by message_room_id) as m2 on m1.created_at=m2.max_created_at "
              + "where (initial_receiver_id=:id or initial_sender_id=:id) "
              + "and (visible_to='BOTH' "
              + "or (visible_to='ONLY_INITIAL_RECEIVER' and initial_receiver_id=:id) "
              + "or (visible_to='ONLY_INITIAL_SENDER' and initial_sender_id =:id))",
      nativeQuery = true,
      countQuery = "select count(*) "
          + "from message_room as mr "
          + "inner join message as m1 on mr.message_room_id=m1.message_room_id "
          + "inner join (select max(created_at) as max_created_at, message_room_id "
          + "from message "
          + "group by message_room_id) as m2 on m1.created_at=m2.max_created_at "
          + "where (initial_receiver_id=:id or initial_sender_id=:id) "
          + "and (visible_to='BOTH' "
          + "or (visible_to='ONLY_INITIAL_RECEIVER' and initial_receiver_id=:id) "
          + "or (visible_to='ONLY_INITIAL_SENDER' and initial_sender_id =:id))")
  Page<MessageRoomsWithLastMessages> findMessageRoomsAndLastMessagesByUserId(@Param("id") Long userId, Pageable pageable);
}