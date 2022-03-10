package ru.protei.portal.core.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.core.model.api.rfidlabel.ApiRFIDNotification;
import ru.protei.portal.core.model.api.rfidlabel.ApiRFIDRequest;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RFIDDevice;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.service.RFIDLabelService;
import ru.protei.portal.core.utils.SessionIdGen;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

@RestController
@RequestMapping(value = "/RSTServer", headers = "Accept=application/xml")
@EnableWebMvc
public class PortalApiRFIDLabelController {
    private static final Logger log = LoggerFactory.getLogger(PortalApiRFIDLabelController.class);

    @Autowired
    private SessionIdGen sidGen;
    @Autowired
    private RFIDLabelService rfidLabelService;

    private static final String STOP_INVENTORY_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><COMMAND xmlns=\"http://schemas.datacontract.org/2004/07/RST_SPEEDWARE\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><ID>%s</ID><DATA>STOP_INVENTORY</DATA><PARAMETER/></COMMAND>";
    private static final String NO_COMMAND_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><COMMAND xmlns=\"http://schemas.datacontract.org/2004/07/RST_SPEEDWARE\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><ID>%s</ID><DATA>NO_COMMAND</DATA><PARAMETER/></COMMAND>";

    @PostMapping(value = "/sendnotify")
    public String updateCase(@RequestBody ApiRFIDNotification rfidNotification,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.info("API | RFID Label  sendnotify: rfidNotification={}", rfidNotification);

        AuthToken rfidToken = makeToken(request);
        RFIDDevice device = rfidLabelService.getOrCreateDeviceByReaderId(rfidToken, rfidNotification.getReaderId())
                .getData();

        if ("START_INVENTORY".equals(rfidNotification.getData())) {
            return String.format(STOP_INVENTORY_RESPONSE, rfidNotification.getId());
        }

        stream(rfidNotification.getParameters())
                .filter(parameter -> parameter.getRegType() == 1)
                .map(parameter -> {
                    RFIDLabel label = new RFIDLabel();
                    label.setEpc(parameter.getEpc());
                    label.setName("Api");
                    label.setLastScanDate(parameter.getTime());
                    label.setRfidDeviceId(device.getId());
                    label.setRfidDevice(device);
                    return label;
                }).forEach(label -> rfidLabelService.saveOrUpdateLastScan(rfidToken, label));

        return "";
    }



    @PostMapping(value = "/getcommand", produces = "application/xml" )
    public String getCommand(@RequestBody ApiRFIDRequest rfidRequest,
                                     HttpServletRequest request, HttpServletResponse response) {

        log.info("API | RFID Label getcommand: rfidRequest={}", rfidRequest);

        return String.format(NO_COMMAND_RESPONSE, rfidRequest.getId());
    }

    private AuthToken makeToken(HttpServletRequest request) {
        AuthToken token = new AuthToken(sidGen.generateId());
        token.setIp(request.getRemoteHost());
        return token;
    }
}