package com.doctorandonuts.taskchampion.sync;

import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr Saturn on 8/8/2015 for TaskChampion
 */
public class Msg {
    private final Map<String, String> _header = new HashMap<>(5);
    private String _payload;
    private String TAG = "TaskWarriorSyncMSG";

    public Msg() {
        this._payload = "";
        // All messages are marked with the version number, so that the messages
        // may be properly evaluated in context.
        this._header.put("client", "TaskChampion");
    }

    public void clear() {
        this._header.clear();
        this._payload = "";
    }

    public void setHeader(final String key, final String value) {
        this._header.put(key, value);
    }

    public void setPayload(final String payload) {
        this._payload = payload;
    }

    public String getPayload() {
        return this._payload;
    }

    public String serialize() {
        setHeader("protocol", "v1");
        setHeader("type", "sync");
        setHeader("org", "Main");
        setHeader("user", "Doctor Andonuts");
        setHeader("key", "cf5a3fa3-5508-4e28-9497-5b44113d45a8");

        final StringBuilder output = new StringBuilder();
        for (final Map.Entry<String, String> entry : this._header.entrySet()) {
            output.append(entry.getKey() + ": " + entry.getValue() + '\n');
        }
        output.append("\n\n" + this._payload + '\n');
        return output.toString();
    }

    public void parse(final String input) throws MalformedInputException {
        this._header.clear();
        this._payload = "";
        final int separator = input.indexOf("\n\n");

        if (separator == -1) {
            throw new MalformedInputException(input.length());
        }

        // Parse Header off
        final String[] header = input.substring(0, separator).split("\n");
        for (final String line : header) {
            final int delimiter = line.indexOf(':');
            if (delimiter == -1) {
                throw new MalformedInputException(line.length());
            }
            this._header.put(line.substring(0, delimiter).trim(), line.substring(delimiter + 1).trim());
        }

        // The rest is the return payload
        this._payload = input.substring(separator + 2).trim();
    }

    public String getHeaderCode() {
        return this._header.get("code");
    }


}
