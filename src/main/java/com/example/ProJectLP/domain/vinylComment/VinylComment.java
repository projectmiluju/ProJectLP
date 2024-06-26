package com.example.ProJectLP.domain.vinylComment;

import com.example.ProJectLP.domain.TimeStamped;
import com.example.ProJectLP.domain.member.Member;
import com.example.ProJectLP.domain.vinyl.Vinyl;
import com.example.ProJectLP.dto.request.VinylCommentRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class VinylComment extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinyl_id", nullable = false)
    private Vinyl vinyl;

    public void update(VinylCommentRequestDto vinylCommentRequestDto) {
        this.content = vinylCommentRequestDto.getContent();
    }
}
