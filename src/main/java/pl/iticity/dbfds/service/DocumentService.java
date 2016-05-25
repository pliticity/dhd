package pl.iticity.dbfds.service;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mysema.query.types.Predicate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import pl.iticity.dbfds.model.*;
import pl.iticity.dbfds.repository.DocumentActivityRepository;
import pl.iticity.dbfds.repository.DocumentInfoRepository;
import pl.iticity.dbfds.security.Principal;
import pl.iticity.dbfds.util.PrincipalUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class DocumentService extends AbstractService<DocumentInfo, DocumentInfoRepository> {

    @Autowired
    private DocumentActivityRepository documentActivityRepository;

    public void favourite(String id, boolean val){
        final Principal current = PrincipalUtils.getCurrentPrincipal();
        DocumentInfo info = repo.findOne(id);
        if(info.getFavourites()!=null){
            DocumentFavourite docFav = Iterables.find(info.getFavourites(), new com.google.common.base.Predicate<DocumentFavourite>() {
                @Override
                public boolean apply(@Nullable DocumentFavourite documentFavourite) {
                    return current.getId().equals(documentFavourite.getPrincipal().getId());
                }
            },null);
            if(docFav!=null && !val){
                info.getFavourites().remove(docFav);
            }else if(docFav==null && val){
                docFav = new DocumentFavourite();
                docFav.setPrincipal(current);
                info.getFavourites().add(docFav);
            }
        }else if(val){
            info.setFavourites(Lists.<DocumentFavourite>newArrayList());
            DocumentFavourite docFav = new DocumentFavourite();
            docFav.setPrincipal(current);
            info.getFavourites().add(docFav);
        }
        repo.save(info);
    }

    public DocumentInfo copyDocument(String docId) {
        DocumentInfo documentInfo = repo.findOne(docId);
        documentInfo = documentInfo.clone();
        documentInfo.setMasterDocumentNumber(getNextMasterDocumentNumber());
        documentInfo.setDocumentNumber(String.valueOf(documentInfo.getMasterDocumentNumber()));
        return repo.save(documentInfo);
    }

    public void removeDocument(String docId) {
        DocumentInfo documentInfo = repo.findOne(docId);
        documentInfo.setRemoved(true);
        repo.save(documentInfo);
    }

    public DocumentInfo create(DocumentInfo documentInfo) {
        Domain current = PrincipalUtils.getCurrentDomain();
        documentInfo.setDomain(current);
        return repo.save(documentInfo);
    }

    public DocumentInfo createNewDocumentInfo() {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setMasterDocumentNumber(getNextMasterDocumentNumber());
        documentInfo.setDocumentNumber(String.valueOf(documentInfo.getMasterDocumentNumber()));
        documentInfo.setCreatedBy(PrincipalUtils.getCurrentPrincipal());
        documentInfo.setCreationDate(new Date());
        return documentInfo;
    }

    public Long getNextMasterDocumentNumber() {
        List<DocumentInfo> docs = repo.findByDomain(PrincipalUtils.getCurrentDomain());
        if (docs == null || docs.isEmpty()) {
            return 1l;
        } else {
            return new Long(docs.size() + 1);
        }
    }

    public List<DocumentInfo> findByCreatedBy(Principal principal) {
        return repo.findByCreatedByAndRemovedIsFalse(principal);
    }

    public List<DocumentInfo> findAll() {
        return repo.findByDomainAndRemovedIsFalse(PrincipalUtils.getCurrentDomain());
    }

    public List<DocumentInfo> findRecent() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        Iterable<String> ids = Iterables.transform(documentActivityRepository.findByPrincipalAndTypeAndDateGreaterThanOrderByDateAsc(PrincipalUtils.getCurrentPrincipal(), DocumentActivity.ActivityType.OPENED, lastMonth.toDate()), new Function<DocumentActivity, String>() {
            @Nullable
            @Override
            public String apply(@Nullable DocumentActivity documentActivity) {
                return documentActivity.getDocumentInfo().getId();
            }
        });
        return Lists.newArrayList(repo.findAll(ids));
    }

    public List<DocumentInfo> findByPredicate(Predicate predicate) {
        return Lists.newArrayList(repo.findAll(predicate));
    }

    public List<FileInfo> appendFile(String documentId, FileInfo fileInfo) {
        DocumentInfo documentInfo = repo.findOne(documentId);
        documentInfo.getFiles().add(fileInfo);
        repo.save(documentInfo);
        return documentInfo.getFiles();
    }

    public DocumentInfo getById(String id) {
        DocumentInfo documentInfo = repo.findOne(id);
        DocumentActivity activity = documentActivityRepository.findByDocumentInfoAndPrincipalAndType(documentInfo,PrincipalUtils.getCurrentPrincipal(), DocumentActivity.ActivityType.OPENED);
        if(activity==null) {
            activity = new DocumentActivity(documentInfo, DocumentActivity.ActivityType.OPENED, PrincipalUtils.getCurrentPrincipal(), new Date());
        }else{
            activity.setDate(new Date());
        }
        documentActivityRepository.save(activity);
        if(documentInfo.getFavourites()!=null){
            DocumentFavourite fav = Iterables.find(documentInfo.getFavourites(), new com.google.common.base.Predicate<DocumentFavourite>() {
                @Override
                public boolean apply(@Nullable DocumentFavourite documentFavourite) {
                    return PrincipalUtils.getCurrentPrincipal().getId().equals(documentFavourite.getPrincipal().getId());
                }
            },null);
            documentInfo.setFavourite(fav!=null);
        }else{
            documentInfo.setFavourite(false);
        }
        return documentInfo;
    }

    public List<DocumentInfo> findMy(){
        return repo.findByCreatedByAndRemovedIsFalse(PrincipalUtils.getCurrentPrincipal());
    }

    public List<DocumentInfo> findFavourite(){
        return repo.findByFavourites_Principal(PrincipalUtils.getCurrentPrincipal());
    }
}
