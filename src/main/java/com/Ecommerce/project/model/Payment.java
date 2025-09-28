package com.Ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Order order;

    @NonNull
    @Size(min = 4,message = "payment method  must contains at least 4 characters")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgStatus;
    private  String pgResponseMessage;
    private String pgName;

//    this is special constructor without order for payment gateway
    public Payment(String paymentMethod,String pgPaymentId,String pgStatus,String pgResponseMessage,String pgName) {

        this.paymentMethod=paymentMethod;
        this.pgResponseMessage=pgResponseMessage;
        this.pgPaymentId=pgPaymentId;
        this.pgStatus=pgStatus;
        this.pgName=pgName;
    }
}

