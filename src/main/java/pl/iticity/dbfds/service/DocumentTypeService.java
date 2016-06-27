package pl.iticity.dbfds.service;

import org.springframework.stereotype.Service;
import pl.iticity.dbfds.model.DocumentType;
import pl.iticity.dbfds.model.Domain;
import pl.iticity.dbfds.repository.DocumentTypeRepository;
import pl.iticity.dbfds.security.AuthorizationProvider;
import pl.iticity.dbfds.security.Role;
import pl.iticity.dbfds.util.PrincipalUtils;

import java.util.List;

@Service
public class DocumentTypeService extends AbstractService<DocumentType,DocumentTypeRepository>{

    public List<DocumentType> findByDomain(Domain domain, boolean onlyActive){
        if(domain==null){
            domain = PrincipalUtils.getCurrentDomain();
        }
        if(onlyActive){
            return repo.findByDomainAndActiveIsTrue(domain);
        }
        return repo.findByDomain(domain);
    }

    public List<DocumentType> addDocType(DocumentType documentType,Domain domain){
        documentType.setDomain(domain);
        documentType.setActive(true);
        repo.save(documentType);
        return findByDomain(domain,false);
    }

    public List<DocumentType> toggleDocType(String id, boolean toggle){
        DocumentType type = repo.findOne(id);
        AuthorizationProvider.hasRole(Role.ADMIN,type.getDomain());
        type.setActive(toggle);
        repo.save(type);
        return findByDomain(type.getDomain(),false);
    }

}