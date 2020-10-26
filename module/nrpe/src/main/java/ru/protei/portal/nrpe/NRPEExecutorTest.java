package ru.protei.portal.nrpe;

import java.util.Arrays;
import java.util.List;

public class NRPEExecutorTest implements NRPEExecutor {
    @Override
    public List<String> execute(String request) {
        if (request.contains("192.168.100.90")) {
            return Arrays.asList(
                    "ARPING 192.168.100.90 from 192.168.0.254 lan",
                    "Unicast reply from 192.168.100.90 [38:D5:47:19:24:D5] 0.669ms",
                    "Unicast reply from 192.168.100.90 [38:D5:47:19:24:D5] 0.662ms",
                    "Unicast reply from 192.168.100.90 [38:D5:47:19:24:D5] 0.667ms",
                    "Sent 3 probes (2 broadcast(s))",
                    "Received 1 response(s)",
                    "0");
        } else {
            return Arrays.asList(
                    "NRPE: Unable to read output",
                    "3"
            );
        }
    }
}
