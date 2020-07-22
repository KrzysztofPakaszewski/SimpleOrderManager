package example.SimpleOrderManager.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_table")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_name")
    private String order_name;

    @Column(name = "buyer_name")
    private String buyer_name;

    @Column(name = "buyer_surname")
    private String buyer_surname;

    @Column(name = "date")
    private Date date;

    public Order(String order_name, String buyer_name, String buyer_surname, Date date){
        this.order_name = order_name;
        this.buyer_name = buyer_name;
        this.buyer_surname = buyer_surname;
        this.date = date;
    }
}
