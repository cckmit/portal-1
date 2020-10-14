package ru.protei.portal.test.nrpe;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.nrpe.NRPEProcessor;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;
import ru.protei.portal.core.model.dict.En_NRPEStatus;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEHostReachable;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEHostUnreachable;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEIncorrectParams;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEServerUnavailable;

import java.util.Arrays;
import java.util.List;

public class NRPETest {
    @Test
    public void parseHostReachable() {
        List<String> responseList = Arrays.asList(
                "ARPING 192.168.100.90 from 192.168.0.254 lan",
                "Unicast reply from 192.168.100.90 [38:D5:47:19:24:D5] 0.669ms",
                "Unicast reply from 192.168.100.90 [38:D5:47:19:24:D5] 0.662ms",
                "Unicast reply from 192.168.100.90 [38:D5:47:19:24:D5] 0.667ms",
                "Sent 3 probes (2 broadcast(s))",
                "Received 1 response(s)",
                "0"
        );

        NRPEResponse response = NRPEProcessor.parse(responseList);
        Assert.assertTrue(response instanceof NRPEHostReachable);

        NRPEHostReachable nrpeResponse = (NRPEHostReachable)response;
        Assert.assertEquals(En_NRPEStatus.HOST_REACHABLE, nrpeResponse.getNRPEStatus());
        Assert.assertEquals("192.168.100.90", nrpeResponse.getIpTarget());
        Assert.assertEquals("192.168.0.254", nrpeResponse.getIpSource());
        Assert.assertEquals(3, nrpeResponse.getProbes());
        Assert.assertEquals(2, nrpeResponse.getBroadcasts());
        Assert.assertEquals(1, nrpeResponse.getResponses());

        Assert.assertFalse(nrpeResponse.isIpConflict());

        Assert.assertEquals("192.168.100.90", nrpeResponse.getProbeInfos().get(0).getIp());
        Assert.assertEquals("38:D5:47:19:24:D5", nrpeResponse.getProbeInfos().get(0).getMac());
        Assert.assertEquals("0.669ms", nrpeResponse.getProbeInfos().get(0).getTime());

        Assert.assertEquals("192.168.100.90", nrpeResponse.getProbeInfos().get(1).getIp());
        Assert.assertEquals("38:D5:47:19:24:D5", nrpeResponse.getProbeInfos().get(1).getMac());
        Assert.assertEquals("0.662ms", nrpeResponse.getProbeInfos().get(1).getTime());

        Assert.assertEquals("192.168.100.90", nrpeResponse.getProbeInfos().get(2).getIp());
        Assert.assertEquals("38:D5:47:19:24:D5", nrpeResponse.getProbeInfos().get(2).getMac());
        Assert.assertEquals("0.667ms", nrpeResponse.getProbeInfos().get(2).getTime());
    }

    @Test
    public void parseHostReachableIpConflict() {
        List<String> responseList = Arrays.asList(
                "ARPING 192.168.100.50 from 192.168.0.254 lan",
                "Unicast reply from 192.168.0.50 [00:07:ED:05:0D:90] 0.721ms",
                "Unicast reply from 192.168.0.50 [00:07:ED:05:0E:FC] 0.739ms",
                "Unicast reply from 192.168.0.50 [28:3B:82:7F:DE:E0] 1.796ms",
                "Sent 3 probes (2 broadcast(s))",
                "Received 1 response(s)",
                "0"
        );

        NRPEResponse response = NRPEProcessor.parse(responseList);
        Assert.assertTrue(response instanceof NRPEHostReachable);

        NRPEHostReachable nrpeResponse = (NRPEHostReachable)response;
        Assert.assertEquals(En_NRPEStatus.HOST_REACHABLE, nrpeResponse.getNRPEStatus());
        Assert.assertEquals("192.168.100.50", nrpeResponse.getIpTarget());
        Assert.assertEquals("192.168.0.254", nrpeResponse.getIpSource());
        Assert.assertEquals(3, nrpeResponse.getProbes());
        Assert.assertEquals(2, nrpeResponse.getBroadcasts());
        Assert.assertEquals(1, nrpeResponse.getResponses());

        Assert.assertTrue(nrpeResponse.isIpConflict());

        Assert.assertEquals("192.168.0.50", nrpeResponse.getProbeInfos().get(0).getIp());
        Assert.assertEquals("00:07:ED:05:0D:90", nrpeResponse.getProbeInfos().get(0).getMac());
        Assert.assertEquals("0.721ms", nrpeResponse.getProbeInfos().get(0).getTime());

        Assert.assertEquals("192.168.0.50", nrpeResponse.getProbeInfos().get(1).getIp());
        Assert.assertEquals("00:07:ED:05:0E:FC", nrpeResponse.getProbeInfos().get(1).getMac());
        Assert.assertEquals("0.739ms", nrpeResponse.getProbeInfos().get(1).getTime());

        Assert.assertEquals("192.168.0.50", nrpeResponse.getProbeInfos().get(2).getIp());
        Assert.assertEquals("28:3B:82:7F:DE:E0", nrpeResponse.getProbeInfos().get(2).getMac());
        Assert.assertEquals("1.796ms", nrpeResponse.getProbeInfos().get(2).getTime());
    }

    @Test
    public void parseHostUnreachable() {
        List<String> responseList = Arrays.asList(
                "ARPING 192.168.100.91 from 192.168.0.254 lan",
                "Sent 4 probes (3 broadcast(s))",
                "Received 0 response(s)",
                "1"
        );

        NRPEResponse response = NRPEProcessor.parse(responseList);
        Assert.assertTrue(response instanceof NRPEHostUnreachable);

        NRPEHostUnreachable nrpeResponse = (NRPEHostUnreachable)response;
        Assert.assertEquals(En_NRPEStatus.HOST_UNREACHABLE, nrpeResponse.getNRPEStatus());
        Assert.assertEquals("192.168.100.91", nrpeResponse.getIpTarget());
        Assert.assertEquals("192.168.0.254", nrpeResponse.getIpSource());
        Assert.assertEquals(4, nrpeResponse.getProbes());
        Assert.assertEquals(3, nrpeResponse.getBroadcasts());
        Assert.assertEquals(0, nrpeResponse.getResponses());
    }

    @Test
    public void parseServerUnavailable() {
        List<String> responseList = Arrays.asList(
                "connect to address 192.168.0.254 port 5666: Connection refused",
                "connect to host router.protei.ru port 5666: Connection refused",
                "2"
        );

        NRPEResponse response = NRPEProcessor.parse(responseList);
        Assert.assertTrue(response instanceof NRPEServerUnavailable);

        NRPEServerUnavailable nrpeResponse = (NRPEServerUnavailable)response;
        Assert.assertEquals(En_NRPEStatus.SERVER_UNAVAILABLE, nrpeResponse.getNRPEStatus());
        Assert.assertEquals("192.168.0.254", nrpeResponse.getIp());
        Assert.assertEquals("5666", nrpeResponse.getPort());
        Assert.assertEquals("router.protei.ru", nrpeResponse.getHost());
    }

    @Test
    public void parseIncorrectParam() {
        List<String> responseList = Arrays.asList(
                "NRPE: Unable to read output",
                "3"
        );

        NRPEResponse response = NRPEProcessor.parse(responseList);
        Assert.assertTrue(response instanceof NRPEIncorrectParams);

        NRPEIncorrectParams nrpeResponse = (NRPEIncorrectParams)response;
        Assert.assertEquals(En_NRPEStatus.INCORRECT_PARAMS, nrpeResponse.getNRPEStatus());
        Assert.assertEquals("NRPE: Unable to read output", nrpeResponse.getMessage());
    }

    @Test
    public void requestReachable() {
        NRPEResponse response = nrpeProcessor.request("192.168.100.90", "check_nrpe %s; echo $?");

        Assert.assertTrue(response instanceof NRPEHostReachable);

        NRPEHostReachable nrpeResponse = (NRPEHostReachable)response;
        Assert.assertEquals(En_NRPEStatus.HOST_REACHABLE, nrpeResponse.getNRPEStatus());
        Assert.assertEquals("192.168.100.90", nrpeResponse.getIpTarget());
    }

    @Test
    public void requestIncorrectParam() {
        NRPEResponse response = nrpeProcessor.request("^788*^7&^*akk", "check_nrpe %s; echo $?");

        Assert.assertTrue(response instanceof NRPEIncorrectParams);

        NRPEIncorrectParams nrpeResponse = (NRPEIncorrectParams)response;
        Assert.assertEquals(En_NRPEStatus.INCORRECT_PARAMS, nrpeResponse.getNRPEStatus());
    }

    private final NRPEProcessor nrpeProcessor = new NRPEProcessor(new NRPEExecutorTest());
}
