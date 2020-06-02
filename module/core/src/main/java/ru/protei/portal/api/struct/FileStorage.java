package ru.protei.portal.api.struct;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import ru.protei.portal.core.model.struct.FileStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.YearMonth;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by bondarenko on 25.04.17.
 */
public class FileStorage {

    private String storagePath;
    private String authentication;
    private static Logger logger = LoggerFactory.getLogger(FileStorage.class);

    public FileStorage(String storagePath, String user, String password){
        this.storagePath = storagePath;
        authentication = new String(
                Base64.getEncoder().encode((user +":"+ password).getBytes())
        );
    }

    /**
     * Saves file to storage
     * @return Saved file's path or IOException
     */
    public String save(String fileName, FileStream fileStream) throws IOException {

        logger.debug("save: fileName=" + fileName);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String currentYearMonth = YearMonth.now().toString();
            String filePath = currentYearMonth + "/" + fileName;
            HttpUriRequest fileCreationRequest = buildFileCreationRequest(filePath, fileStream);

            HttpUriRequest buildCheckFolderExistsRequest = buildCheckFolderExists(currentYearMonth);



            CloseableHttpResponse execute = httpClient.execute(buildCheckFolderExistsRequest);
            int checkFolderExistsStatus = getStatus(execute);

            if (checkFolderExistsStatus == HttpStatus.NOT_FOUND.value()) {
                HttpUriRequest folderCreationRequest = buildFolderCreationRequest(currentYearMonth);
                int folderCreationStatus = getStatus(httpClient.execute(folderCreationRequest));

                if (folderCreationStatus != HttpStatus.CREATED.value()) {
                    throw new IOException("Unable create folder on fileStorage. status code " + folderCreationStatus);
                }
            }

            try (CloseableHttpResponse fileCreationResponse = httpClient.execute(fileCreationRequest)) {
                logFileCreationResponse(fileName, fileCreationResponse);
                int fileCreationStatus = getStatus(fileCreationResponse);

                if (fileCreationStatus == HttpStatus.CREATED.value()) {
                    return filePath;
                } else {
                    throw new IOException("Unable upload file to fileStorage. status code " + fileCreationStatus);
                }
            }
        }
    }

    private void doWithClose(Supplier<CloseableHttpResponse> responseSupplier, Consumer<CloseableHttpResponse> responseConsumer) throws IOException {
        try (CloseableHttpResponse response = responseSupplier.get()) {
            responseConsumer.accept(response);
        }
    }

    private HttpUriRequest buildCheckFolderExists(String folderName) {
        RequestBuilder request = RequestBuilder.create("GET");
        request.setUri(storagePath + folderName);
        request.addHeader("Authorization", "Basic " + authentication);
        return request.build();
    }

    private HttpUriRequest buildFileCreationRequest(String filePath, FileStream fileStream) throws IOException {
        RequestBuilder request = RequestBuilder.create("PUT");
        request.setUri(storagePath + filePath);
        request.addHeader("Authorization", "Basic " + authentication);
        request.setEntity(fileStream2InputStreamEntity(fileStream));
//        request.addHeader("Translate", "f");
        return request.build();
    }

    private HttpUriRequest buildFolderCreationRequest(String folderName) throws IOException{
        RequestBuilder request = RequestBuilder.create("MKCOL");
        request.setUri(storagePath + folderName);
        request.addHeader("Authorization", "Basic " + authentication);
        return request.build();
    }

    /**
     * @return {@link FileStorage.File} or NULL otherwise
     */
    public File getFile(String filePath){

        logger.debug("getFile: filePath=" + filePath);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(storagePath + filePath).openConnection();
            conn.setRequestProperty("Authorization", "Basic "+ authentication);
            conn.connect();

            logger.debug("getFile: filePath=" + filePath + ", statusCode=" + conn.getResponseCode());

            if(conn.getResponseCode() == HttpStatus.OK.value())
                return new File(conn.getContentType(), IOUtils.toBufferedInputStream(conn.getInputStream()));

        }catch (IOException e){
            logger.error("getFile", e);
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * @return true if file deleted or false otherwise
     */
    public boolean deleteFile(String filePath){

        logger.debug("deleteFile: filePath=" + filePath);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            RequestBuilder request = RequestBuilder.create("DELETE");
            request.setUri(storagePath + filePath);
            request.addHeader("Authorization", "Basic " + authentication);

            try (CloseableHttpResponse response = httpClient.execute(request.build())) {
                logger.debug("deleteFile: filePath=" + filePath + ", statusCode=" + getStatus(response));
                return HttpStatus.valueOf(getStatus(response)).is2xxSuccessful();
            }

        }catch (IOException e){
            logger.error("deleteFile", e);
        }
        return false;
    }

    public class File{
        private String contentType;
        private InputStream data;

        public File(String contentType, InputStream data) {
            this.contentType = contentType;
            this.data = data;
        }

        public String getContentType() {
            return contentType;
        }

        public InputStream getData() {
            return data;
        }
    }

    private InputStreamEntity fileStream2InputStreamEntity(FileStream fileStream) {
        return new InputStreamEntity(
                fileStream.getInputStream(),
                fileStream.getFileSize(),
                ContentType.create(fileStream.getContentType())
        );
    }

    private int getStatus(HttpResponse response){
        return response.getStatusLine().getStatusCode();
    }

    private void logFileCreationResponse(String fileName, CloseableHttpResponse fileCreationResponse) {
        StringBuilder sb = new StringBuilder();
        int statusCode = getStatus(fileCreationResponse);
        sb.append("save: fileName=").append(fileName);
        sb.append(", fileCreationResponse");
        sb.append(", statusCode=").append(statusCode);
        if (statusCode != HttpStatus.CREATED.value()) {
            sb.append(", headers={");
            for (Header header : fileCreationResponse.getAllHeaders()) {
                for (HeaderElement element : header.getElements()) {
                    if (element == null) continue;
                    sb.append("(");
                    sb.append(element.getName());
                    sb.append(":");
                    sb.append(element.getValue());
                    sb.append("), ");
                }
            }
            sb.append("}");
        }
        logger.debug(sb.toString());
    }
}
