package comidev.comistore.components.product.doc;

import java.util.List;

import comidev.comistore.components.product.request.ProductCreate;
import comidev.comistore.components.product.response.ProductDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ProductDoc {
    @Operation(summary = "findAllOrFields - Devuelve lista de productos, por nombre y/o categoria")
    List<ProductDetails> getAllProductsOrFields(String name, String category);

    @Operation(summary = "findById - Devuelve producto por id")
    ProductDetails getProductById(Long id);

    @Operation(summary = "save - Registra un producto", security = @SecurityRequirement(name = "bearer-key"))
    ProductDetails registerProduct(ProductCreate productReq);
}
