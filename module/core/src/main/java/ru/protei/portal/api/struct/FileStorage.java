package ru.protei.portal.api.struct;

import org.apache.commons.io.IOUtils;
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
import java.net.URLEncoder;
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){

            String currentYearMonth = YearMonth.now().toString();
            String filePath = currentYearMonth +"/"+ encodePath(fileName);
            HttpUriRequest fileCreationRequest = buildFileCreationRequest(filePath, data);

            int fileCreationStatus = getStatus(httpClient.execute(fileCreationRequest));

            if(fileCreationStatus == HttpStatus.NOT_FOUND.value()){ //folder not exists
                HttpUriRequest folderCreationRequest = buildFolderCreationRequest(currentYearMonth);
                int folderCreationStatus = getStatus(httpClient.execute(folderCreationRequest));

                if(folderCreationStatus == HttpStatus.CREATED.value())
                    fileCreationStatus = getStatus(httpClient.execute(fileCreationRequest));
                else
                    throw new IOException("Unable create folder on fileStorage. status code "+ folderCreationStatus);
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
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(storagePath + filePath).openConnection();
            conn.setRequestProperty("Authorization", "Basic "+ authentication);
            conn.connect();
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            RequestBuilder request = RequestBuilder.create("DELETE");
            request.setUri(storagePath + filePath);
            request.addHeader("Authorization", "Basic " + authentication);

            CloseableHttpResponse response = httpClient.execute(request.build());
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
        return URLEncoder.encode(path, "UTF-8").replace("+", "%20");
    }
    private int getStatus(HttpResponse response){
        return response.getStatusLine().getStatusCode();
    }

}
