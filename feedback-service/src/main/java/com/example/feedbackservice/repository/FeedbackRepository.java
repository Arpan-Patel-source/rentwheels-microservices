package com.example.feedbackservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.feedbackservice.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
