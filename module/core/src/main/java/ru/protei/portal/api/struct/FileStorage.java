package ru.protei.portal.api.struct;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.YearMonth;
import java.util.Base64;

/**
 * Created by bondarenko on 25.04.17.
 */
public class FileStorage {

    private String storagePath;
    private String authentication;
    private static Logger logger = Logger.getLogger(FileStorage.class);

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
    public String save(String fileName, InputStream data) throws IOException{

        logger.debug("save: fileName=" + fileName);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()){

            String currentYearMonth = YearMonth.now().toString();
            String filePath = currentYearMonth +"/"+ encodePath(fileName);
            HttpUriRequest fileCreationRequest = buildFileCreationRequest(filePath, data);

            CloseableHttpResponse fileCreationResponse = httpClient.execute(fileCreationRequest);
            logFileCreationResponse(fileName, "1", fileCreationResponse);
            int fileCreationStatus = getStatus(fileCreationResponse);

            if(fileCreationStatus == HttpStatus.NOT_FOUND.value()){ //folder not exists
                HttpUriRequest folderCreationRequest = buildFolderCreationRequest(currentYearMonth);
                int folderCreationStatus = getStatus(httpClient.execute(folderCreationRequest));
                logger.debug("save: fileName=" + fileName + ", folderCreationRequest, statusCode=" + folderCreationStatus);

                if (folderCreationStatus == HttpStatus.CREATED.value()) {
                    fileCreationResponse = httpClient.execute(fileCreationRequest);
                    logFileCreationResponse(fileName, "2", fileCreationResponse);
                    fileCreationStatus = getStatus(fileCreationResponse);
                } else {
                    throw new IOException("Unable create folder on fileStorage. status code " + folderCreationStatus);
                }
            }

            if(fileCreationStatus == HttpStatus.CREATED.value())
                return filePath;
            else
                throw new IOException("Unable upload file to fileStorage. status code "+ fileCreationStatus);
        }
    }


    private HttpUriRequest buildFileCreationRequest(String filePath, InputStream data) throws IOException{
        RequestBuilder request = RequestBuilder.create("PUT");
        request.setUri(storagePath + filePath);
        request.addHeader("Authorization", "Basic " + authentication);
        request.addHeader("Content-Type", "text/plain");
        request.addHeader("Translate", "f");
        request.setEntity(new InputStreamEntity(data));
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
            logger.error(e);
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

            CloseableHttpResponse response = httpClient.execute(request.build());

            logger.debug("deleteFile: filePath=" + filePath + ", statusCode=" + getStatus(response));

            return HttpStatus.valueOf(getStatus(response)).is2xxSuccessful();
        }catch (IOException e){
            logger.error(e);
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

    private String encodePath(String path) throws UnsupportedEncodingException{
        final Base64.Encoder enc = Base64.getUrlEncoder();
        int lastDotPos = path.lastIndexOf('.');
        int firstUnderscorePos = path.indexOf('_');
        if (lastDotPos == -1) {
            lastDotPos = path.length();
        }
        return path.substring(0, firstUnderscorePos + 1)
                + enc.encodeToString(path.substring(firstUnderscorePos + 1, lastDotPos).getBytes())
                + path.substring(lastDotPos);
    }
    private int getStatus(HttpResponse response){
        return response.getStatusLine().getStatusCode();
    }

    private void logFileCreationResponse(String fileName, String attempt, CloseableHttpResponse fileCreationResponse) {
        StringBuilder sb = new StringBuilder();
        int statusCode = getStatus(fileCreationResponse);
        sb.append("save: fileName=").append(fileName);
        sb.append(", fileCreationResponse#").append(attempt);
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
