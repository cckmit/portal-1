package ru.protei.portal.test.nrpe;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.nrpe.NRPERequest;
import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.NRPEStatus;
import ru.protei.portal.core.nrpe.response.NRPEIncorrectParams;

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
                "Sent 3 probes (1 broadcast(s))",
                "Received 3 response(s)",
                "0"
        );
    }

    @Test
    public void parseHostReachableIpConflict() {
        List<String> responseList = Arrays.asList(
                "ARPING 192.168.100.90 from 192.168.0.254 lan",
                "Unicast reply from 192.168.0.50 [00:07:ED:05:0D:90] 0.721ms",
                "Unicast reply from 192.168.0.50 [00:07:ED:05:0E:FC] 0.739ms",
                "Unicast reply from 192.168.0.50 [28:3B:82:7F:DE:E0] 1.796ms",
                "Sent 1 probes (1 broadcast(s))",
                "Received 3 response(s)",
                "0"
        );
    }

    @Test
    public void parseHostUnreachable() {
        List<String> responseList = Arrays.asList(
                "ARPING 192.168.100.91 from 192.168.0.254 lan",
                "Sent 4 probes (4 broadcast(s))",
                "Received 0 response(s)",
                "1"
        );
    }

    @Test
    public void parseServerUnavailable() {
        List<String> responseList = Arrays.asList(
                "connect to address 192.168.0.254 port 5666: Connection refused",
                "connect to host router.protei.ru port 5666: Connection refused",
                "2"
        );
    }

    @Test
    public void parseIncorrectParam() {
        List<String> responseList = Arrays.asList(
                "NRPE: Unable to read output",
                "3"
        );

        NRPEResponse response = NRPERequest.parse(responseList);
        Assert.assertTrue(response instanceof NRPEIncorrectParams);

        NRPEIncorrectParams nrpeIncorrectParams = (NRPEIncorrectParams)response;
        Assert.assertEquals(NRPEStatus.INCORRECT_PARAMS, nrpeIncorrectParams.getNRPEStatus());
        Assert.assertEquals("NRPE: Unable to read output", nrpeIncorrectParams.getMessage());
    }
}
