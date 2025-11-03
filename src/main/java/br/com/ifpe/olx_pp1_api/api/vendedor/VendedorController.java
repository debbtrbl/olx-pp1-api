package br.com.ifpe.olx_pp1_api.api.vendedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.olx_pp1_api.modelo.vendedor.Vendedor;
import br.com.ifpe.olx_pp1_api.modelo.vendedor.VendedorService;

@RestController
@RequestMapping("/api/vendedor")
@CrossOrigin
public class VendedorController {
    
   @Autowired
   private VendedorService vendedorService;

   @PostMapping
   public ResponseEntity<Vendedor> save(@RequestBody VendedorRequest request) {

       Vendedor vendedor = vendedorService.save(request.build());
       return new ResponseEntity<Vendedor>(vendedor, HttpStatus.CREATED);
   }

}
