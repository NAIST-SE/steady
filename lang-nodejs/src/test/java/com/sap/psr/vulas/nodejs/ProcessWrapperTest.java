package com.sap.psr.vulas.nodejs;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ProcessWrapperTest {
    @Test
    public void testIllegalChar() throws ProcessWrapperException {
        ProcessWrapper pw = new ProcessWrapper();
        try {
            pw.setCommand(Paths.get("npm"), "list", "\\asas");
        } catch (ProcessWrapperException e) {
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testLegalChar() throws ProcessWrapperException {
        ProcessWrapper pw = new ProcessWrapper();
        pw.setCommand(Paths.get("npm"), "list", "-json");
    }
}