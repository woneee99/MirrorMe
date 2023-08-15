package com.mirror.backend.api.entity;

import com.mirror.backend.api.entity.keys.EmotionKey;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "emotion_count")
public class EmotionCount {
    @EmbeddedId
    private EmotionKey emotionKey;
    private int emotionCount;

    @Builder
    public EmotionCount(EmotionKey emotionKey, int emotionCount) {
        this.emotionKey = emotionKey;
        this.emotionCount = emotionCount;
    }

    public void update(int emotionCount) {
        this.emotionCount = emotionCount;
    }
}
