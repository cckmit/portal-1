package ru.protei.portal.core.controller.cloud;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.InvalidFileNameException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.core.model.helper.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;

@RestController
public class ImageController {

    @PostConstruct
    public void onInit() {
        uploadServlet.setFileItemFactory(new DiskFileItemFactory());
        uploadServlet.setHeaderEncoding("UTF-8");
    }

    @RequestMapping(
            value = "/convertImageToBase64",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"
    )
    @ResponseBody
    public String convertImageToBase64(HttpServletRequest req, HttpServletResponse res) {
        try {
            for (FileItem fileItem : uploadServlet.parseRequest(req)) {
                if (fileItem.isFormField()) {
                    continue;
                }

                String mimeType = tryGetMimeType(fileItem);
                if (StringUtils.isEmpty(mimeType) || !mimeType.contains("/")) {
                    logger.info("convertImageToBase64(): mimeType is not recognized, mimeType={}", mimeType);
                    return "";
                }

                if (!mimeTypeMatchesImage(mimeType)) {
                    logger.info("convertImageToBase64(): mimeType is not image, mimeType={}", mimeType);
                    return "";
                }

                return String.format("data:%s;base64,%s", mimeType, Base64Utils.encodeToString(fileItem.get()));
            }
            return "";
        } catch (FileUploadException e) {
            logger.error("convertImageToBase64", e);
            return "";
        }
    }

    private String tryGetMimeType(FileItem fileItem) {
        String mimeType = null;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(fileItem.getInputStream());
        } catch (IOException ignore) {}
        if (mimeType == null && StringUtils.isNotBlank(fileItem.getName())) {
            try {
                mimeType = URLConnection.guessContentTypeFromName(fileItem.getName());
            } catch (InvalidFileNameException ignore) {}
        }
        if (mimeType == null) {
            mimeType = fileItem.getContentType();
        }
        return mimeType;
    }

    private boolean mimeTypeMatchesImage(String mimeType) {
        String originalType = mimeType.split("/")[0];
        return "image".equals(originalType);
    }

    private ServletFileUpload uploadServlet = new ServletFileUpload();
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
}
