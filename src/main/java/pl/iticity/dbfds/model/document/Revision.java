package pl.iticity.dbfds.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.mapping.DBRef;
import pl.iticity.dbfds.security.Principal;
import pl.iticity.dbfds.util.PrincipalUtils;

import java.io.IOException;
import java.util.Date;

public class Revision {

    @DBRef
    @JsonIgnoreProperties(value = {"password", "lastName", "firstName", "country", "phone", "company", "role", "domain"})
    private Principal principal;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date date;

    private RevisionSymbol revision;

    @JsonIgnore
    private String data;

    public Revision() {
    }

    public Revision(RevisionSymbol revision, DocumentInformationCarrier data, Date date) throws JsonProcessingException {
        this.principal = PrincipalUtils.getCurrentPrincipal();
        this.date = date;
        this.revision = revision;
        ObjectMapper mapper = new ObjectMapper();
        this.data = mapper.writeValueAsString(data);
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RevisionSymbol getRevision() {
        return revision;
    }

    public void setRevision(RevisionSymbol revision) {
        this.revision = revision;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @JsonIgnore
    public DocumentInformationCarrier getRevisionData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getData(), DocumentInformationCarrier.class);
    }
}
