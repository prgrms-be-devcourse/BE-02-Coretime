package com.prgrms.coretime.post.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 65535)
    private String content;

    @Column(nullable = false)
    private Integer nextAnonymousSeq;

    @Column(nullable = false)
    private Boolean isAnonymous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
