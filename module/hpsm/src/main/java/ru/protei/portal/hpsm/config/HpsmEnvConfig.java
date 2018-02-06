package ru.protei.portal.hpsm.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import ru.protei.portal.core.utils.ConfigParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 25.04.17.
 */
@XStreamAlias("eventAssemblyConfig-config")
public class HpsmEnvConfig {

    public static final HpsmEnvConfig load(String file) throws IOException {
        return ConfigParser.parse(file, true,
                HpsmEnvConfig.class,
                ServiceConfig.class,
                OutboundChannel.class,
                InboundChannel.class,
                CompanyBranchEntry.class);
    }



    @XStreamImplicit
    @XStreamAlias("service-instance")
    private List<ServiceConfig> instanceList;

    @XStreamAlias("company-map")
    private List<CompanyBranchEntry> companyMapEntries;

    private HpsmEnvConfig() {
        instanceList = new ArrayList<>();
    }


    public List<ServiceConfig> getInstanceList() {
        return instanceList == null ? Collections.emptyList() : instanceList;
    }

    public List<CompanyBranchEntry> getCompanyMapEntries() {
        return companyMapEntries;
    }

    @XStreamAlias("company-map-entry")
    public static class CompanyBranchEntry {

        @XStreamAlias("branch-name")
        private String branchName;

        @XStreamAlias("company-id")
        private long companyId;

        public CompanyBranchEntry() {
        }

        public String getBranchName() {
            return branchName;
        }

        public long getCompanyId() {
            return companyId;
        }
    }

    public static class ServiceConfig {

        @XStreamAlias("id")
        @XStreamAsAttribute
        private String id;

        @XStreamAlias("inbound-channel")
        private InboundChannel inboundChannel;

        @XStreamAlias("outbound-channel")
        private OutboundChannel outboundChannel;

        public ServiceConfig() {
        }

        public ServiceConfig(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public InboundChannel getInboundChannel() {
            return inboundChannel;
        }

        public OutboundChannel getOutboundChannel() {
            return outboundChannel;
        }

        public ServiceConfig outbound (String senderAddress, String sendTo) {
            this.outboundChannel = new OutboundChannel(senderAddress, sendTo);
            return this;
        }

        public ServiceConfig inbound (String url) {
            this.inboundChannel = new InboundChannel(url);
            return this;
        }
    }


    public static class OutboundChannel {

        @XStreamAlias("sender-address")
        private String senderAddress;

        @XStreamAlias("send-to")
        private String sendTo;

        @XStreamAlias("default-contact-email")
        private String defaultContactEmail;

        @XStreamAlias("default-contact-dname")
        private String defaultContactName;



        public OutboundChannel() {
        }


        public OutboundChannel(String senderAddress, String sendTo) {
            this.senderAddress = senderAddress;
            this.sendTo = sendTo;
        }

        public String getSenderAddress() {
            return senderAddress;
        }

        public String getSendTo() {
            return sendTo;
        }

        public String getDefaultContactEmail() {
            return defaultContactEmail;
        }

        public String getDefaultContactName() {
            return defaultContactName;
        }
    }


    public static class InboundChannel {
        @XStreamAlias("url")
        private String url;

        public InboundChannel() {
        }

        public InboundChannel(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
