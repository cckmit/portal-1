package ru.protei.portal.hpsm.struct;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmSetup {

    private String senderAddress;

    private String mailServerHost;
    private int mailServerPort;

//    public String hpsmAddress;

//    private Map<String,ControlPoint> controlPointMap;
//    private Map<Long, ControlPoint> backMap;

    public HpsmSetup() {
//        backMap = new HashMap<>();
//        controlPointMap = new HashMap<>();
    }

//    public ControlPoint controlPoint (String email) {
//        return controlPointMap.get(email.toLowerCase());
//    }
//
//    public ControlPoint controlPoint (Long companyId) {
//        return backMap.get(companyId);
//    }
//
//    public void addCompanyMap (String emailFrom, Map<String, Long> companyMap) {
////        companyMap.values().forEach();
//    }

//    public ControlPoint addControlPoint (String inboundEmail, String outEmail) {
//        return controlPointMap.computeIfAbsent(inboundEmail.toLowerCase(), s -> new ControlPoint(inboundEmail, outEmail));
//    }

    public HpsmSetup sender (String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public HpsmSetup mailServer (String host, int port) {
        this.mailServerHost = host;
        this.mailServerPort = port;
        return this;
    }


    public String getSenderAddress() {
        return senderAddress;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public int getMailServerPort() {
        return mailServerPort;
    }

//    public class ControlPoint {
//
//        String inboundEmail;
//        String outboundEmail;
//
//        Map<String, Long> companyMap;
//
//        ControlPoint (String in, String out) {
//            this.inboundEmail = in;
//            this.outboundEmail = out;
//
//            this.companyMap = new HashMap<>();
//        }
//
//        public Long companyId (String name) {
//            return companyMap.get(name);
//        }
//
//        void addCompany(String name, Long id) {
//            this.companyMap.put(name, id);
//        }
//
//        public String getInboundEmail() {
//            return inboundEmail;
//        }
//
//        public String getOutboundEmail() {
//            return outboundEmail;
//        }
//    }
}
