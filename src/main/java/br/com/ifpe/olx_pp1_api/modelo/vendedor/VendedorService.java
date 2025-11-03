package br.com.ifpe.olx_pp1_api.modelo.vendedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class VendedorService {
    @Autowired
   private VendedorRepository repository;

   @Transactional
   public Vendedor save(Vendedor vendedor) {

       vendedor.setHabilitado(Boolean.TRUE);
       return repository.save(vendedor);
   }


}
