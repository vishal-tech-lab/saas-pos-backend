package com.example.Backend.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Sales {
        @Id
        @GeneratedValue(strategy =GenerationType.IDENTITY)
        private  Long    salesid;
        private  String   billno;
        private  LocalDate date;
        private  String customername;
        private  String itemname;
        private  Double itemprice;
        private  Double  weight;
        private  Double  bag;
        private  Double  total;
           

}
