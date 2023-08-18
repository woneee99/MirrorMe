package com.mirror.backend.api.entity.keys;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
public class EmotionKey implements Serializable {
    private int emotionCode;
    private Long emotionId;

    @Builder
    public EmotionKey(int emotionCode, Long emotionId) {
        this.emotionCode = emotionCode;
        this.emotionId = emotionId;
    }
}
