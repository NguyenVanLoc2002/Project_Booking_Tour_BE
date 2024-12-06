package com.fit.tourservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tours")
public class Tour {
    @Id
    private Long tourId;
    private String name;
    private double price;
    private double oldPrice;
    //    Khoảng thời gian 3 ngay 2 dem
    private int day;
    private int night;
    //Điểm den
    private String destination;
//    private int availableSlot;
    private List<String> urlImage;
    private boolean includePromotions;
}
