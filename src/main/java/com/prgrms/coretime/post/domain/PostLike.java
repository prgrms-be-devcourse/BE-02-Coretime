package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

import javax.persistence.*;

@Entity
@Table(name = "post_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostLike {
    @EmbeddedId
    private PostLikeId postLikeId;

    @MapsId("postId")
    @ManyToOne
    @JoinColumnOrFormula(column =
    @JoinColumn(name = "post_id",
            referencedColumnName = "post_id")
    )
    private Post post;

    @MapsId("userId")
    @ManyToOne
    @JoinColumnOrFormula(column =
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id")
    )
    private User user;

    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
