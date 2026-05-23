    package com.example.Backend.Entity;

    import java.time.LocalDate;
    import java.util.List;


    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SalesRequest {
        private  String   billno;
            private  LocalDate date;
            private  String customername;
            private List<Sales> items;
    }
