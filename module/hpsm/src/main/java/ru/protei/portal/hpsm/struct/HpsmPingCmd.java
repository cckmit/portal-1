package ru.protei.portal.hpsm.struct;

import ru.protei.portal.hpsm.utils.HpsmUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmPingCmd {
    public static final String HPSM_ID = "HPSM";
    public static final String VENDOR_ID = "VENDOR";

    /*
    TEST_SERVER=[HPSM];HPSM_SEND_TIME=[01/05/2016 10:00:00]
    VENDOR_SEND_TIME=[]
     */
    private static final String FMT_REQ = "TEST_SERVER=[HPSM];HPSM_SEND_TIME=[%s]";
    private static final String FMT_RESP = "TEST_SERVER=[VENDOR];HPSM_SEND_TIME=[%s];VENDOR_SEND_TIME=[%s]";


    private boolean isRequest;
    private Date requestTime;
    private Date responseTime;


    public HpsmPingCmd (boolean isRequest) {
        this (isRequest, new Date());
    }

    public HpsmPingCmd (boolean isRequest, Date requestTime) {
        this.isRequest = isRequest;
        this.requestTime = requestTime;
        this.responseTime = isRequest ? null : new Date();
    }

    public boolean isResponse () {
        return !isRequest;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public String getServer() {
        return isRequest ? HPSM_ID : VENDOR_ID;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public HpsmPingCmd response () {
        return new HpsmPingCmd(false, this.requestTime);
    }


    public String toString () {
        return isRequest ? String.format(FMT_REQ, HpsmUtils.formatDate(requestTime))
                : String.format(FMT_RESP, HpsmUtils.formatDate(requestTime), HpsmUtils.formatDate(responseTime));
    }


    public static HpsmPingCmd parse (String x) throws ParseException {
        String server = HpsmUtils.extractOption(x, "TEST_SERVER", null);
        String requestTimeTx = HpsmUtils.extractOption(x, "HPSM_SEND_TIME", null);

        if (server == null || requestTimeTx == null)
            return null;

        String respTime = HpsmUtils.extractOption(x, "VENDOR_SEND_TIME", null);

        HpsmPingCmd result = new HpsmPingCmd(server.equals(HPSM_ID), HpsmUtils.parseDate(requestTimeTx));
        result.responseTime = HpsmUtils.parseDate(respTime);

        return result;
    }

}
