package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.user.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", length = 65535)
    private String content;

    @Column(name = "next_anonymous_seq", nullable = false)
    private Integer nextAnonymousSeq = 1;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", referencedColumnName = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "createdFrom")
    private List<MessageRoom> messageRooms = new ArrayList<>();

    @Builder
    public Post(
            String title,
            String content,
            Boolean isAnonymous,
            Board board,
            User user
    ) {
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.board = board;
        this.user = user;
    }

    public void addComment(Comment comment) {
        comment.setPost(this);
    }

    public void updatePost(PostUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
    }

    public void addAnonymousSeq() {
        nextAnonymousSeq++;
    }

}
