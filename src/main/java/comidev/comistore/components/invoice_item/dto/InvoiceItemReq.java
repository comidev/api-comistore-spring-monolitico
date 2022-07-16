package comidev.comistore.components.invoice_item.dto;

import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemReq {
    @Positive
    private Integer quantity;
    @Positive
    private Long productId;
}
