package ru.protei.portal.core.nrpe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NRPEExecutorTerminal implements NRPEExecutor {
    @Override
    public List<String> execute(String request) {
        String[] bashArgs = new String[] {"/bin/bash", "-c", request};
        ArrayList<String> lines = new ArrayList<>();
        try {
            Process pr = new ProcessBuilder(bashArgs).start();
            BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return lines;
    }
}
