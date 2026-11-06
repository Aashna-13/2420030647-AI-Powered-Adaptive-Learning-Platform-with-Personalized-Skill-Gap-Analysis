package com.skillbridge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "career_questions", indexes = {
        @Index(name = "idx_cq_career", columnList = "careerId"),
        @Index(name = "idx_cq_career_skill", columnList = "careerId, skillCategory")
})
public class CareerQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String careerId;

    @Column(nullable = false, length = 100)
    private String skillCategory;

    @Column(nullable = false, length = 1000)
    private String questionText;

    @Column(nullable = false, length = 500)
    private String optionA;

    @Column(nullable = false, length = 500)
    private String optionB;

    @Column(nullable = false, length = 500)
    private String optionC;

    @Column(nullable = false, length = 500)
    private String optionD;

    @Column(nullable = false)
    private int correctOption; // 0 = A, 1 = B, 2 = C, 3 = D

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionDifficulty difficulty = QuestionDifficulty.BEGINNER;

    @Column(length = 1000)
    private String explanation;

    public CareerQuestion() {}

    public CareerQuestion(
            String careerId,
            String skillCategory,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            int correctOption,
            QuestionDifficulty difficulty,
            String explanation
    ) {
        this.careerId = careerId;
        this.skillCategory = skillCategory;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.difficulty = difficulty;
        this.explanation = explanation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCareerId() { return careerId; }
    public void setCareerId(String careerId) { this.careerId = careerId; }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public int getCorrectOption() { return correctOption; }
    public void setCorrectOption(int correctOption) { this.correctOption = correctOption; }

    public QuestionDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(QuestionDifficulty difficulty) { this.difficulty = difficulty; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
