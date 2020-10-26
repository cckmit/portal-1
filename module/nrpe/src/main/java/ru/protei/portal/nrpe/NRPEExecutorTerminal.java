package ru.protei.portal.nrpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NRPEExecutorTerminal implements NRPEExecutor {
    private static final Logger log = LoggerFactory.getLogger(NRPEExecutorTerminal.class);
    @Override
    public List<String> execute(String request) {
        String[] bashArgs = new String[] {"/bin/bash", "-c", request};
        log.info("request = {}", request);
        ArrayList<String> lines = new ArrayList<>();
        try (
                InputStream inputStream = new ProcessBuilder(bashArgs).start().getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream))) {
            while (true) {
                String line = r.readLine();
                log.info("line = {}", line);
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return lines;
    }
}
