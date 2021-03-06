package pl.iticity.dbfds.service.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import pl.iticity.dbfds.model.document.DocumentInformationCarrier;
import pl.iticity.dbfds.model.Domain;
import pl.iticity.dbfds.model.document.FileInfo;
import pl.iticity.dbfds.security.Principal;
import pl.iticity.dbfds.service.ScopedService;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

public interface FileService extends ScopedService<FileInfo> {

    public void changeName(FileInfo fileInfo);

    public FileInfo findBySymbol(String symbol);

    public FileInfo createFile(Domain domain, String fileName, String mime, InputStream inputStream, Principal principal);

    public FileInfo createFile(Domain domain, String fileName, String mime, InputStream inputStream);

    public String createFileDownloadName(DocumentInformationCarrier documentInformationCarrier, String fname);

    public void updateContent(FileInfo fileInfo);

    public List<FileInfo> removeContent(String docId, final String fileId);

    public void removeContent(FileInfo fileInfo);

    public File getFileForFileInfo(FileInfo fileInfo);

    public File getFileForFileInfo(String fileId);

    public OutputStream getOutputStreamFromContent(FileInfo fileInfo);

    public FileInfo zipFiles(String[] filesIds) throws IOException;

    public List<FileInfo> copyFiles(List<FileInfo> files) throws FileNotFoundException;

    public FileInfo copyFile(FileInfo fileInfo) throws FileNotFoundException;

    public long countByDomain(Domain domain);

    public double countMemoryByDomain(Domain domain);

    public String updateFileInfo(InputStream inputStream, String fileId, String docId) throws JsonProcessingException;

    public String[] getFileNames(String[] ids);

    public File createTempFile();

    public boolean openOnDesktop(String fileId);

    public boolean unlock(String fileId);

    public List<FileInfo> refresh(String fileId);

    public File getDesktopVersion(OperatingSystem os);
}
