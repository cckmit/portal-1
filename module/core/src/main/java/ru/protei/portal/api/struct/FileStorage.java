package ru.protei.portal.api.struct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.YearMonth;
import java.util.Base64;
import java.util.Properties;

/**
 * Created by bondarenko on 25.04.17.
 */
public class FileStorage {

    private static Logger logger = Logger.getLogger(FileStorage.class);
    private static final String FILE_STORAGE_PROPERTIES = "/cloud.properties";
    private static final String STORAGE_PATH;
    private static final String AUTHENTICATION;

    static {
        Properties properties = new Properties();
        try (InputStream stream = FileStorage.class.getResourceAsStream(FILE_STORAGE_PROPERTIES)){
            properties.load(stream);
        }catch (IOException e){
            logger.error(e);
        }
        STORAGE_PATH = properties.getProperty("path");
        AUTHENTICATION = new String(
                Base64.getEncoder().encode((properties.getProperty("user") +":"+ properties.getProperty("password")).getBytes())
        );
    }

    /**
     * Saves file to storage
     * @return Saved file's path or NULL
     */
    public String save(String fileName, byte[] data){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            String currentYearMonth = YearMonth.now().toString();
            String filePath = currentYearMonth +"/"+ fileName;
            int folderCreationStatusCode;
            do{
                int fileCreationStatusCode = saveFile(httpClient, filePath, data)
                        .getStatusLine().getStatusCode();

                if(fileCreationStatusCode == HttpStatus.NOT_FOUND.value()){ //folder not exists
                    RequestBuilder request = RequestBuilder.create("MKCOL");
                    request.setUri(STORAGE_PATH + currentYearMonth);
                    request.addHeader("Authorization", "Basic " + AUTHENTICATION);
                    folderCreationStatusCode = httpClient.execute(request.build()).getStatusLine().getStatusCode();
                }else
                    return fileCreationStatusCode == HttpStatus.CREATED.value()? filePath: null;
            }while (folderCreationStatusCode == HttpStatus.CREATED.value());
        }catch (IOException e){
            logger.error(e);
        }
        return null;
    }


    private HttpResponse saveFile(HttpClient httpClient, String filePath, byte[] data) throws IOException{
        RequestBuilder request = RequestBuilder.create("PUT");
        request.setUri(STORAGE_PATH + filePath);
        request.addHeader("Authorization", "Basic " + AUTHENTICATION);
        request.addHeader("Content-Type", "text/plain");
        request.addHeader("Translate", "f");
        request.setEntity(new ByteArrayEntity(data));
        return httpClient.execute(request.build());
    }

    /**
     * @return {@link FileStorage.File} File or NULL otherwise
     */
    public File getFile(String filePath){
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(STORAGE_PATH + filePath).openConnection();
            conn.setRequestProperty("Authorization", "Basic "+ AUTHENTICATION);
            conn.connect();
            if(conn.getResponseCode() != HttpStatus.OK.value())
                return null;

            File file = new File(conn.getContentType(), IOUtils.toByteArray(conn.getInputStream()));
            conn.disconnect();
            return file;
        }catch (IOException e){
            logger.error(e);
        }
        return null;
    }

    /**
     * @return true if file deleted or false otherwise
     */
    public boolean deleteFile(String filePath){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            RequestBuilder request = RequestBuilder.create("DELETE");
            request.setUri(STORAGE_PATH + filePath);
            request.addHeader("Authorization", "Basic " + AUTHENTICATION);

            CloseableHttpResponse response = httpClient.execute(request.build());
            return HttpStatus.valueOf(response.getStatusLine().getStatusCode()).is2xxSuccessful();
        }catch (IOException e){
            logger.error(e);
        }
        return false;
    }


    public class File{
        private String contentType;
        private byte[] data;

        public File(String contentType, byte[] data) {
            this.contentType = contentType;
            this.data = data;
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getData() {
            return data;
        }
    }

}
