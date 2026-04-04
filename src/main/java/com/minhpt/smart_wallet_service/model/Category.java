package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categrorie_seq")
    @SequenceGenerator(
            name = "categrorie_seq",
            sequenceName = "categrorie_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_eng")
    private String nameEng;

    @Column(name = "type")
    private String type;

    @Column(name = "icon")
    private String icon;

    @Column(name = "color")
    private String color;
}
