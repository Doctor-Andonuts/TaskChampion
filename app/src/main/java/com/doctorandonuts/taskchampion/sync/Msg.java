package com.doctorandonuts.taskchampion.sync;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr Saturn on 8/8/2015 for TaskChampion
 */
public class Msg {
    private final Map<String, String> _header = new HashMap<>(5);
    private String _payload;

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
        final StringBuilder output = new StringBuilder();
        for (final Map.Entry<String, String> entry : this._header.entrySet()) {
            output.append(entry.getKey() + ": " + entry.getValue() + '\n');
        }
        output.append("\n\n" + this._payload + '\n');
        return output.toString();
    }


}
