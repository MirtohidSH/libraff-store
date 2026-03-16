package org.example.libraffstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.TransferStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_store_id", nullable = false)
    private Store fromStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_store_id", nullable = false)
    private Store toStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_employee_id", nullable = false)
    private Employee requestedEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_employee_id")
    private Employee approvedEmployee;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus transferStatus;
}