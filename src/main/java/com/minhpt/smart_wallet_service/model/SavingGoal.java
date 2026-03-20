package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "saving_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingGoal extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saving_goals_id_seq")
    @SequenceGenerator(
            name = "saving_goals_id_seq",
            sequenceName = "saving_goals_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "target_amount")
    private Double targetAmount;

    @Column(name = "current_amount")
    private Double currentAmount;

    @Column(name = "deadline")
    private LocalDateTime deadline;
}
