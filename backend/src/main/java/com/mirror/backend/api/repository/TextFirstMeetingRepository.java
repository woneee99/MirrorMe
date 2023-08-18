package com.mirror.backend.api.repository;

import com.mirror.backend.api.entity.TextFirstMeeting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextFirstMeetingRepository extends CrudRepository<TextFirstMeeting, String> {

}
